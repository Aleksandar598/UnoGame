package bg.sofia.uni.fmi.mjt.server.game;

import bg.sofia.uni.fmi.mjt.exception.*;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;

import java.util.List;

public interface GameManager {

    void createGame(int gameId, String creatorName);

    List<GameController> listGames();

    void joinGame(int gameId, String username) throws MaximumPlayerCountReached, GameHasStartedException;

    void startGame(int gameId, String username) throws PlayerNotFoundException, CannotStartGameException, UnoUserException;

    void leaveGame(String username) throws UnoUserException, PlayerNotFoundException;

    GameController getGameController(int gameId);

    GameController getUserGame(String username);

}
