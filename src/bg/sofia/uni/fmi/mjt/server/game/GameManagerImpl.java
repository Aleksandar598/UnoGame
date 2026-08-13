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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameManagerImpl implements GameManager {

    private final IdGenerator playerIdGenerator;
    private final Map<Integer, GameController> managedGamesById;
    private final Map<Integer, Integer> gameIdByUserId;
    private final Map<String, Player> playerByUsername;
    private static final GameManagerImpl INSTANCE = new GameManagerImpl();


    public static GameManagerImpl getInstance()
    {
        return INSTANCE;
    }

    private GameManagerImpl() {
        playerIdGenerator = new PlayerIdGenerator();
        managedGamesById = new ConcurrentHashMap<>();
        gameIdByUserId = new ConcurrentHashMap<>();
        playerByUsername = new ConcurrentHashMap<>();
    }
    @Override
    public void createGame(int gameId, String creatorName) {
        Player unoPlayer = new UnoPlayer(creatorName, playerIdGenerator.getId());
        GameController controller = new GameControllerImpl(new UnoDeck(new DeckCreator(new CardIdGenerator())), unoPlayer);
        managedGamesById.put(gameId, controller);
        gameIdByUserId.put(unoPlayer.getId(), gameId);
        playerByUsername.put(creatorName, unoPlayer);
    }

    @Override
    public List<GameController> listGames() {
        return List.copyOf(managedGamesById.values());
    }

    @Override
    public void joinGame(int gameId, String username) throws MaximumPlayerCountReached, GameHasStartedException {
        Player unoPlayer = new UnoPlayer(username, playerIdGenerator.getId());
        if (!managedGamesById.containsKey(gameId)) {
            throw new IllegalArgumentException("Game with id " + gameId + " does not exist");
        }
        GameController controller = managedGamesById.get(gameId);
        controller.addPlayer(unoPlayer);
        gameIdByUserId.put(unoPlayer.getId(), gameId);
        playerByUsername.put(username, unoPlayer);
    }

    @Override
    public void startGame(int gameId, String username) throws PlayerNotFoundException, CannotStartGameException, UnoUserException {
        GameController controller = managedGamesById.get(gameId);
        if (!username.equals(controller.getCreator().getName())) {
            throw new UnoUserException("Player is not the creator");
        }
        controller.startGame();
    }

    @Override
    public void leaveGame(String username) throws UnoUserException, PlayerNotFoundException {
        Player unoPlayer;
        GameController controller;
        try {
            unoPlayer = playerByUsername.get(username);
            controller = managedGamesById.get(unoPlayer.getId());
        } catch (RuntimeException e) {
            throw new UnoUserException("Could not remove player form game", e);
        }
        controller.removePlayer(unoPlayer.getId());
    }

    @Override
    public GameController getGameController(int gameId) {
        if (managedGamesById.containsKey(gameId)) {
            return managedGamesById.get(gameId);
        }
        throw new IllegalArgumentException("Game with id " + gameId + " does not exist");
    }

    @Override
    public GameController getUserGame(String username) {
        if (managedGamesById.containsKey(gameIdByUserId.get(username))) {
            return managedGamesById.get(gameIdByUserId.get(username));
        }
        throw new IllegalArgumentException("Game not found");
    }
}
