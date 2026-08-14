package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.exception.GameHasStartedException;
import bg.sofia.uni.fmi.mjt.exception.MaximumPlayerCountReached;
import bg.sofia.uni.fmi.mjt.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.server.exception.ExceptionLogger;
import bg.sofia.uni.fmi.mjt.server.exception.GameNotFoundException;
import bg.sofia.uni.fmi.mjt.server.exception.MissingArgumentsException;
import bg.sofia.uni.fmi.mjt.server.game.GameInfo;
import bg.sofia.uni.fmi.mjt.server.game.GameManager;
import bg.sofia.uni.fmi.mjt.server.game.GameManagerImpl;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import bg.sofia.uni.fmi.mjt.server.user.UserManagerImpl;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;

public class JoinGameCommand extends AbstractCommand {

    String gameId;
    String displayName;

    private static final String NOT_LOGGED = "You are not logged in";
    private static final String MAX_PLAYER_COUNT = "Game you want to join is full";
    private static final String GAME_STARTED = "Game has already started";
    private static final String GAME_NOT_FOUND = "Game has not been found";
    private static final String SUCCESS = "Successfully joined game";

    @Override
    public String execute(String input, SocketChannel socket) throws IOException {
        UserManager userManager = UserManagerImpl.getInstance();
        try {
            Player player = userManager.bindSocketToPlayer(socket, displayName);
            GameManager gameManager = GameManagerImpl.getInstance();
            gameManager.joinGame(gameId, player);

        } catch (UserNotLoggedInException e) {
            ExceptionLogger.logException(e);
            return NOT_LOGGED;
        } catch (MaximumPlayerCountReached e) {
            userManager.unbindPlayer(socket);
            ExceptionLogger.logException(e);
            return MAX_PLAYER_COUNT;

        } catch (GameHasStartedException e) {
            userManager.unbindPlayer(socket);
            ExceptionLogger.logException(e);
            return GAME_STARTED;
        } catch (GameNotFoundException e) {
            userManager.unbindPlayer(socket);
            ExceptionLogger.logException(e);
            return GAME_NOT_FOUND;
        }
        return SUCCESS;
    }

    private void parse(String input, SocketChannel socket) throws MissingArgumentsException, UserNotLoggedInException {
        Map<String, String> args = parser.parse(input);

        if (!args.containsKey("game-id")) {
            throw new MissingArgumentsException("Need gameid and displayname to join a game");
        }
        if (args.containsKey("display-name")) {
            displayName = args.get("display-name");
        }
        else {
            UserManager userManager = UserManagerImpl.getInstance();
            displayName = userManager.getUsername(socket);
        }
        gameId = args.get("game-id");
        displayName = args.get("display-name");
    }
}
