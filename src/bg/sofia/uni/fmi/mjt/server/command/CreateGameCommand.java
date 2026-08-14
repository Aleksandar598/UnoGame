package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.server.exception.ExceptionLogger;
import bg.sofia.uni.fmi.mjt.server.exception.MissingArgumentsException;
import bg.sofia.uni.fmi.mjt.server.game.GameManager;
import bg.sofia.uni.fmi.mjt.server.game.GameManagerImpl;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import bg.sofia.uni.fmi.mjt.server.user.UserManagerImpl;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Map;

public class CreateGameCommand extends AbstractCommand {

    private int playerCount;
    private String gameId;

    private static final String MISSING_ARGS = "Need player count and game name";
    private static final String USER_NOT_LOGGED_IN = "You are not logged in";
    private static final String SUCCESS = "Successfully created game";
    private static final int DEFAULT_MAX_PLAYER_COUNT = 2;

    @Override
    public String execute(String input, SocketChannel socket) throws IOException {
        try {
            parse(input);
            GameManager gameManager = GameManagerImpl.getInstance();
            UserManager userManager = UserManagerImpl.getInstance();
            String creator = userManager.getUsername(socket);
            gameManager.createGame(this.gameId, creator, this.playerCount);

        } catch (MissingArgumentsException e) {
            ExceptionLogger.logException(e);
            return MISSING_ARGS;
        } catch (UserNotLoggedInException e) {
            ExceptionLogger.logException(e);
            return USER_NOT_LOGGED_IN;
        }
        return SUCCESS;
    }

    private void parse(String input) throws MissingArgumentsException {
        Map<String, String> info = parser.parse(input);
        if (!info.containsKey("game-id")) {
            throw new MissingArgumentsException("Need number-of-players and game-id");
        }
        try {
            this.playerCount = Integer.parseInt(info.get("number-of-players"));
        } catch (Exception e) {
            throw new MissingArgumentsException("Number of players must be an integer", e);
        }
        if (info.containsKey("number-of-players")) {
            this.playerCount = Integer.parseInt(info.get("number-of-players"));
        }
        else this.playerCount = DEFAULT_MAX_PLAYER_COUNT;
        this.gameId = info.get("game-id");
    }
}
