package bg.sofia.uni.fmi.mjt.command.serverCommand;

import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.command.gameCommand.GameCommand;
import bg.sofia.uni.fmi.mjt.command.gameCommand.GameCommandFactory;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.command.serverCommand.broadcast.GameEventBroadcaster;
import bg.sofia.uni.fmi.mjt.server.exception.ExceptionLogger;
import bg.sofia.uni.fmi.mjt.server.game.GameManager;
import bg.sofia.uni.fmi.mjt.server.game.GameManagerImpl;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import bg.sofia.uni.fmi.mjt.server.user.UserManagerImpl;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class InGameCommand extends AbstractCommand {

    private final UserManager userManager;
    private final GameManager gameManager;
    private final GameCommandCreator gameCommandCreator;

    public InGameCommand() {
        this(UserManagerImpl.getInstance(), GameManagerImpl.getInstance(), GameCommandFactory::createGameCommand);
    }

    InGameCommand(UserManager userManager, GameManager gameManager, GameCommandCreator gameCommandCreator) {
        if (userManager == null || gameManager == null || gameCommandCreator == null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }
        this.userManager = userManager;
        this.gameManager = gameManager;
        this.gameCommandCreator = gameCommandCreator;
    }

    @Override
    public String execute(String input, SocketChannel socket) throws IOException {
        try {
            Player player = userManager.getPlayer(socket);
            if (!gameManager.isInGame(player.getId())) {
                return "You are not currently in a game";
            }

            GameController controller = gameManager.getUserGame(player);
            GameCommand gameCommand = gameCommandCreator.create(input, controller, player.getId());

            String response = gameCommand.execute();
            if (controller.hasEnded()) {
                gameManager.markGameAsEnded(player);
            }
            GameEventBroadcaster.broadcast(response, gameManager, input, player);
            return response;
        } catch (UnoUserException | UserNotLoggedInException | IllegalArgumentException e) {
            ExceptionLogger.logException(e);
            return e.getMessage();
        }
    }
}
