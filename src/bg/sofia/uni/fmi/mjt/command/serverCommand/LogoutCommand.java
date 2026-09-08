package bg.sofia.uni.fmi.mjt.command.serverCommand;

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

public class LogoutCommand extends AbstractCommand {

    private static final String NOT_LOGGED_IN = "Not logged in";
    private static final String PLAYER_NOT_FOUND = "Unexpected error occurred";
    private static final String SUCCESS = "Logout Successful";
    private final UserManager userManager;
    private final GameManager gameManager;

    public LogoutCommand() {
        this(UserManagerImpl.getInstance(), GameManagerImpl.getInstance());
    }

    LogoutCommand(UserManager userManager, GameManager gameManager) {
        if (userManager == null || gameManager == null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }
        this.userManager = userManager;
        this.gameManager = gameManager;
    }

    @Override
    public String execute(String input, SocketChannel socket) throws IOException {
        if (!userManager.isLoggedIn(socket)) {
            return NOT_LOGGED_IN;
        }
        Player player;
        try {
            player = userManager.getPlayer(socket);
        } catch (UnoUserException e) {
            // A logged-in session does not necessarily have a game player yet.
            userManager.logout(socket);
            return SUCCESS;
        } catch (UserNotLoggedInException e) {
            return NOT_LOGGED_IN;
        }
        try {
            if (gameManager.isInGame(player.getId())) {
                gameManager.leaveGame(player);
            }
        } catch (PlayerNotFoundException | UnoUserException e) {
            ExceptionLogger.logException(e);
            return PLAYER_NOT_FOUND;
        }
        userManager.logout(socket);
        return SUCCESS;
    }

}
