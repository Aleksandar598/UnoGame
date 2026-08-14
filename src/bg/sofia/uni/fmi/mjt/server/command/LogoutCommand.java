package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.exception.*;
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

    @Override
    public String execute(String input, SocketChannel socket) throws IOException {
        UserManager userManager = UserManagerImpl.getInstance();
        GameManager gameManager = GameManagerImpl.getInstance();
        try {
            Player player = userManager.getPlayer(socket);
            gameManager.leaveGame(player);
        } catch (PlayerNotFoundException | UnoUserException e) {
            ExceptionLogger.logException(e);
            return PLAYER_NOT_FOUND;
        } catch (UserNotLoggedInException e) {
            ExceptionLogger.logException(e);
            return NOT_LOGGED_IN;
        }
        userManager.logout(socket);
        return SUCCESS;
    }

}
