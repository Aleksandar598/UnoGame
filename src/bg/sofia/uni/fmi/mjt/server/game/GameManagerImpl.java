package bg.sofia.uni.fmi.mjt.server.game;

import bg.sofia.uni.fmi.mjt.exception.*;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.game.controller.GameControllerImpl;
import bg.sofia.uni.fmi.mjt.game.deck.UnoDeck;
import bg.sofia.uni.fmi.mjt.game.deck.creator.DeckCreator;
import bg.sofia.uni.fmi.mjt.id.CardIdGenerator;
import bg.sofia.uni.fmi.mjt.id.IdGenerator;
import bg.sofia.uni.fmi.mjt.id.PlayerIdGenerator;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.player.UnoPlayer;
import bg.sofia.uni.fmi.mjt.server.exception.GameNotFoundException;
import bg.sofia.uni.fmi.mjt.server.exception.UserInGameException;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import bg.sofia.uni.fmi.mjt.server.user.UserManagerImpl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameManagerImpl implements GameManager {

    private final Map<String, GameController> managedGamesById;
    private final Map<String, GameInfo> gamesInfoById;
    private final Map<Integer, String> gameIdByUserId;
    private final Set<Integer> playersInGame;
    private static final GameManagerImpl INSTANCE = new GameManagerImpl();


    public static GameManagerImpl getInstance()
    {
        return INSTANCE;
    }

    private GameManagerImpl() {
        managedGamesById = new ConcurrentHashMap<>();
        gameIdByUserId = new ConcurrentHashMap<>();
        gamesInfoById = new ConcurrentHashMap<>();
        playersInGame = new HashSet<>();

    }
    @Override
    public void createGame(String gameId, String creatorName, int playerCount) {
        GameController controller = new GameControllerImpl(new UnoDeck(new DeckCreator(new CardIdGenerator())));
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
    public void joinGame(String gameId, Player player) throws MaximumPlayerCountReached, GameHasStartedException, GameNotFoundException {
        if (!managedGamesById.containsKey(gameId)) {
            throw new GameNotFoundException("Game with id " + gameId + " does not exist");
        }
        if (playersInGame.contains(player.getId())) {
            throw new UserInGameException("User is already in game");
        }
        GameController controller = managedGamesById.get(gameId);
        controller.addPlayer(player);
        gameIdByUserId.put(player.getId(), gameId);
        playersInGame.add(player.getId());
    }

    @Override
    public void startGame(Player player, String username) throws PlayerNotFoundException, CannotStartGameException, UnoUserException {
        GameController controller = this.getUserGame(player);
        UserManager userManager = UserManagerImpl.getInstance();
        GameInfo info = this.gamesInfoById.get(gameIdByUserId.get(player.getId()));
        if (!player.getName().equals(info.creatorUsername())) {
            throw new UnoUserException("Player is not the creator");
        }
        controller.startGame();
    }

    @Override
    public void leaveGame(Player player) throws UnoUserException, PlayerNotFoundException {
        GameController controller;
        try {

            controller = managedGamesById.get(gameIdByUserId.get(player.getId()));
        } catch (RuntimeException e) {
            throw new UnoUserException("Could not remove player from game", e);
        }
        controller.removePlayer(player.getId());
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
        int userId = player.getId();
        if (managedGamesById.containsKey(gameIdByUserId.get(userId))) {
            return managedGamesById.get(gameIdByUserId.get(userId));
        }
        throw new IllegalArgumentException("Game not found");
    }

    @Override
    public void notifyAllInAGame(Player player, String message) {
        UserManager userManager = UserManagerImpl.getInstance();

    }
}
