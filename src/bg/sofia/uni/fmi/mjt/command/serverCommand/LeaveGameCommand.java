package bg.sofia.uni.fmi.mjt.command.serverCommand;

import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.server.game.GameManager;
import bg.sofia.uni.fmi.mjt.server.game.GameManagerImpl;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import bg.sofia.uni.fmi.mjt.server.user.UserManagerImpl;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class LeaveGameCommand extends AbstractCommand {

    private static final String SUCCESS = "left the game";
    private final UserManager userManager;
    private final GameManager gameManager;

    public LeaveGameCommand() {
        this(UserManagerImpl.getInstance(), GameManagerImpl.getInstance());
    }

    LeaveGameCommand(UserManager userManager, GameManager gameManager) {
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
            gameManager.leaveGame(player);
            gameManager.notifyAllInAGame(player, player.getName() + SUCCESS);
            return SUCCESS;
        } catch (UserNotLoggedInException exception) {
            return "User is not logged in";
        } catch (PlayerNotFoundException | UnoUserException exception) {
            return exception.getMessage();
        }
    }
}
