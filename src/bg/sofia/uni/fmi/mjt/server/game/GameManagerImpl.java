package bg.sofia.uni.fmi.mjt.server.game;

import bg.sofia.uni.fmi.mjt.exception.CannotStartGameException;
import bg.sofia.uni.fmi.mjt.exception.GameHasStartedException;
import bg.sofia.uni.fmi.mjt.exception.MaximumPlayerCountReached;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.game.controller.GameControllerImpl;
import bg.sofia.uni.fmi.mjt.game.deck.UnoDeck;
import bg.sofia.uni.fmi.mjt.game.deck.creator.DeckCreator;
import bg.sofia.uni.fmi.mjt.id.CardIdGenerator;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.server.exception.GameNotFoundException;
import bg.sofia.uni.fmi.mjt.server.exception.UserInGameException;
import bg.sofia.uni.fmi.mjt.server.network.MessageSender;
import bg.sofia.uni.fmi.mjt.server.network.ServerMessageGateway;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import bg.sofia.uni.fmi.mjt.server.user.UserManagerImpl;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GameManagerImpl implements GameManager {

    private final Map<String, GameController> managedGamesById;
    private final Map<String, GameInfo> gamesInfoById;
    private final Map<Integer, String> gameIdByUserId;
    private final Set<Integer> playersInGame;
    private final UserManager userManager;
    private final MessageSender messageSender;
    private static final GameManagerImpl INSTANCE = new GameManagerImpl();
    private static final String GAME_FILE_PATH = "Ended_games_metadata.txt";

    public static GameManagerImpl getInstance() {
        return INSTANCE;
    }

    GameManagerImpl() {
        this(UserManagerImpl.getInstance(), ServerMessageGateway::send);
    }

    GameManagerImpl(UserManager userManager, MessageSender messageSender) {
        if (userManager == null || messageSender == null) {
            throw new IllegalArgumentException("Game manager dependencies cannot be null");
        }
        managedGamesById = new ConcurrentHashMap<>();
        gameIdByUserId = new ConcurrentHashMap<>();
        gamesInfoById = new ConcurrentHashMap<>();
        playersInGame = ConcurrentHashMap.newKeySet();
        this.userManager = userManager;
        this.messageSender = messageSender;
    }

    @Override
    public synchronized void createGame(String gameId, String creatorName, int playerCount) {
        if (managedGamesById.containsKey(gameId)) {
            throw new IllegalArgumentException("Game ID already exists");
        }
        GameController controller = new GameControllerImpl(new UnoDeck(new DeckCreator(new CardIdGenerator())),
                playerCount);
        GameInfo info = new GameInfo(gameId, creatorName, GameStatus.AVAILABLE, new ArrayList<>(), playerCount);
        managedGamesById.put(gameId, controller);
        gamesInfoById.put(gameId, info);
    }

    @Override
    public List<GameInfo> listGames() {
        return List.copyOf(gamesInfoById.values());
    }

    @Override
    public GameInfo getGame(String gameId) throws GameNotFoundException {
        if (!gamesInfoById.containsKey(gameId)) {
            throw new GameNotFoundException(gameId);
        }
        return gamesInfoById.get(gameId);
    }

    @Override
    public synchronized void joinGame(String gameId, Player player) throws MaximumPlayerCountReached,
            GameHasStartedException, GameNotFoundException {
        if (!managedGamesById.containsKey(gameId)) {
            throw new GameNotFoundException("Game with id " + gameId + " does not exist");
        }
        if (playersInGame.contains(player.getId())) {
            throw new UserInGameException("User is already in game");
        }
        GameController controller = managedGamesById.get(gameId);
        GameInfo info = this.gamesInfoById.get(gameId);
        if (info.maxPlayerCount() == info.players().size()) {
            throw new MaximumPlayerCountReached("Maximum player count reached");
        }
        List<Integer> players = new ArrayList<>(info.players());
        players.add(player.getId());
        controller.addPlayer(player);
        gameIdByUserId.put(player.getId(), gameId);
        playersInGame.add(player.getId());
        gamesInfoById.put(gameId, updatePlayersGameInfo(info, players));
    }

    @Override
    public synchronized void startGame(Player player, String username) throws CannotStartGameException,
            UnoUserException {
        GameController controller = this.getUserGame(player);
        String gameId = this.gameIdByUserId.get(player.getId());
        GameInfo info = this.gamesInfoById.get(gameId);
        if (!username.equals(info.creatorUsername())) {
            throw new UnoUserException("Player is not the creator");
        }
        controller.startGame();
        this.gamesInfoById.put(gameId, updateStatusGameInfo(info, GameStatus.STARTED));
    }

    @Override
    public synchronized void leaveGame(Player player) throws UnoUserException, PlayerNotFoundException {
        if (player == null) {
            throw new IllegalArgumentException("player cannot be null");
        }
        String gameId = gameIdByUserId.get(player.getId());
        if (gameId == null) {
            throw new UnoUserException("Player is not in a game");
        }

        GameInfo info = this.gamesInfoById.get(gameId);
        GameController controller = managedGamesById.get(gameId);
        if (controller == null) {
            throw new UnoUserException("Game not found");
        }
        List<Integer> players = new ArrayList<>(info.players());
        players.remove(Integer.valueOf(player.getId()));
        controller.removePlayer(player.getId());
        gameIdByUserId.remove(player.getId());
        playersInGame.remove(player.getId());
        if ( players.size() <= 1 && controller.hasEnded()) {
            this.gamesInfoById.put(gameId, new GameInfo(gameId, info.creatorUsername(),
                    GameStatus.ENDED, players, info.maxPlayerCount()));
        } else {
            this.gamesInfoById.put(gameId, updatePlayersGameInfo(info, players));
        }
        if (players.isEmpty()) {
            managedGamesById.remove(gameId);
            gamesInfoById.remove(gameId);
        }
    }

    @Override
    public void markGameAsEnded(Player player) throws UnoUserException {
        String gameId = gameIdByUserId.get(player.getId());

        if (gameId == null) {
            throw new UnoUserException("Player is not in a game");
        }

        GameInfo info = gamesInfoById.get(gameId);
        if (info == null) {
            throw new UnoUserException("Game not found");
        }

        gamesInfoById.put(gameId, updateStatusGameInfo(info, GameStatus.ENDED));
    }

    @Override
    public GameController getGameController(String gameId) throws GameNotFoundException {
        if (managedGamesById.containsKey(gameId)) {
            return managedGamesById.get(gameId);
        }
        throw new GameNotFoundException("Game with id " + gameId + " does not exist");
    }

    @Override
    public GameController getUserGame(Player player) {
        String gameId = gameIdByUserId.get(player.getId());
        if (gameId == null || !managedGamesById.containsKey(gameId)) {
            throw new IllegalArgumentException("Game not found");
        }
        return managedGamesById.get(gameId);
    }

    @Override
    public void notifyAllInAGame(Player player, String message) {
        if (player == null || message == null) {
            throw new IllegalArgumentException("Player and message cannot be null");
        }

        String gameId = gameIdByUserId.get(player.getId());
        if (gameId == null) {
            return;
        }

        GameInfo gameInfo = gamesInfoById.get(gameId);
        if (gameInfo == null) {
            return;
        }

        for (SocketChannel socket : userManager.getConnectedSockets(gameInfo.players())) {
            messageSender.send(socket, message);
        }
    }

    @Override
    public boolean isInGame(int playerId) {
        return playersInGame.contains(playerId);
    }

    @Override
    public String getEndedGameInfo(String gameId) {
        if (gameId == null || !gamesInfoById.containsKey(gameId)) {
            return "Game not found";
        }
        GameInfo gameInfo = gamesInfoById.get(gameId);
        if (gameInfo.status() != GameStatus.ENDED) {
            return "Game has not ended";
        }
        return endedGameInfoString(gameInfo);
    }

    private String endedGameInfoString(GameInfo gameInfo) {
        GameController controller = managedGamesById.get(gameInfo.gameId());
        StringBuilder result = new StringBuilder();

        result.append("Game ID: ").append(gameInfo.gameId()).append(System.lineSeparator())
                .append("Creator: ").append(gameInfo.creatorUsername()).append(System.lineSeparator())
                .append("Status: ").append(gameInfo.status().getLabel()).append(System.lineSeparator())
                .append("Players: ").append(gameInfo.players()).append(System.lineSeparator())
                .append("Maximum players: ").append(gameInfo.maxPlayerCount());

        if (controller != null) {
            result.append(System.lineSeparator())
                    .append("Winners:").append(System.lineSeparator())
                    .append(controller.getWinnerLog())
                    .append(System.lineSeparator())
                    .append("Played cards:").append(System.lineSeparator())
                    .append(controller.getPlayedCardsLog());
        }

        return result.toString();
    }

    private GameInfo updatePlayersGameInfo(GameInfo oldInfo, List<Integer> playerIds) {
        return new GameInfo(oldInfo.gameId(), oldInfo.creatorUsername(),
                oldInfo.status(), playerIds, oldInfo.maxPlayerCount());
    }

    private GameInfo updateStatusGameInfo(GameInfo oldInfo, GameStatus status) {
        return new GameInfo(oldInfo.gameId(), oldInfo.creatorUsername(),
                status, oldInfo.players(), oldInfo.maxPlayerCount());
    }
}
