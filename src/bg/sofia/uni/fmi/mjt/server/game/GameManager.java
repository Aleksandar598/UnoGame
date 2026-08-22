package bg.sofia.uni.fmi.mjt.server.game;

import bg.sofia.uni.fmi.mjt.exception.CannotStartGameException;
import bg.sofia.uni.fmi.mjt.exception.GameHasStartedException;
import bg.sofia.uni.fmi.mjt.exception.MaximumPlayerCountReached;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.server.exception.GameNotFoundException;

import java.util.List;

public interface GameManager {

    void createGame(String gameId, String creatorName, int playerCount);

    List<GameInfo> listGames();

    GameInfo getGame(String gameId) throws GameNotFoundException;

    void joinGame(String gameId, Player player) throws MaximumPlayerCountReached,
            GameHasStartedException, GameNotFoundException;

    void startGame(Player player, String username) throws PlayerNotFoundException,
            CannotStartGameException, UnoUserException;

    void leaveGame(Player player) throws UnoUserException, PlayerNotFoundException;

    void markGameAsEnded(Player player) throws UnoUserException;

    GameController getGameController(String gameId) throws GameNotFoundException;

    GameController getUserGame(Player player);

    void notifyAllInAGame(Player player, String message);

    boolean isInGame(int playerId);

    String getEndedGameInfo(String gameId);

}
