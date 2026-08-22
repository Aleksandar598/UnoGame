package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.exception.CannotStartGameException;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.server.exception.ExceptionLogger;
import bg.sofia.uni.fmi.mjt.server.game.GameManager;
import bg.sofia.uni.fmi.mjt.server.game.GameManagerImpl;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import bg.sofia.uni.fmi.mjt.server.user.UserManagerImpl;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class StartGameCommand extends AbstractCommand {

    private static final String NOT_LOGGED = "User is not logged in";
    private static final String NOT_JOINED_GAME = "User has not joined game";
    private static final String USER_NOT_CREATED = "User has not been created";
    private static final String CANNOT_START_GAME = "Cannot start game";
    private static final String GAME_HAS_STARTED = "Game has started!";
    private final UserManager userManager;
    private final GameManager gameManager;

    public StartGameCommand() {
        this(UserManagerImpl.getInstance(), GameManagerImpl.getInstance());
    }

    StartGameCommand(UserManager userManager, GameManager gameManager) {
        if (userManager == null || gameManager == null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }
        this.userManager = userManager;
        this.gameManager = gameManager;
    }

    @Override
    public String execute(String input, SocketChannel socket) throws IOException {
        try {
            Player player = userManager.getPlayer(socket);
            gameManager.startGame(player, userManager.getUsername(socket));
            gameManager.notifyAllInAGame(player, GAME_HAS_STARTED);
            return null; //We already sent info through notifyAll;

        } catch (UserNotLoggedInException e) {
            ExceptionLogger.logException(e);
            return NOT_LOGGED;
        } catch (UnoUserException e) {
            ExceptionLogger.logException(e);
            return USER_NOT_CREATED;
        } catch (PlayerNotFoundException e) {
            ExceptionLogger.logException(e);
            return NOT_JOINED_GAME;
        } catch (CannotStartGameException e) {
            ExceptionLogger.logException(e);
            return CANNOT_START_GAME;
        }
    }
}
