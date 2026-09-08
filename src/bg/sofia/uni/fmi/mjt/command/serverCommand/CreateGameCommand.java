package bg.sofia.uni.fmi.mjt.command.serverCommand;

import bg.sofia.uni.fmi.mjt.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.server.exception.ExceptionLogger;
import bg.sofia.uni.fmi.mjt.server.exception.MissingArgumentsException;
import bg.sofia.uni.fmi.mjt.server.game.GameManager;
import bg.sofia.uni.fmi.mjt.server.game.GameManagerImpl;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import bg.sofia.uni.fmi.mjt.server.user.UserManagerImpl;
import bg.sofia.uni.fmi.mjt.command.serverCommand.parser.ArgumentsParser;
import bg.sofia.uni.fmi.mjt.command.serverCommand.parser.ArgumentsParserImpl;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Map;

public class CreateGameCommand extends AbstractCommand {

    private int playerCount;
    private String gameId;
    private final GameManager gameManager;
    private final UserManager userManager;
    private final ArgumentsParser argumentsParser;

    private static final String MISSING_ARGS = "Need player count and game name";
    private static final String USER_NOT_LOGGED_IN = "You are not logged in";
    private static final String SUCCESS = "Successfully created game";
    private static final int DEFAULT_MAX_PLAYER_COUNT = 2;

    public CreateGameCommand() {
        this(GameManagerImpl.getInstance(), UserManagerImpl.getInstance(), new ArgumentsParserImpl());
    }

    CreateGameCommand(GameManager gameManager, UserManager userManager, ArgumentsParser argumentsParser) {
        if (gameManager == null || userManager == null || argumentsParser == null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }
        this.gameManager = gameManager;
        this.userManager = userManager;
        this.argumentsParser = argumentsParser;
    }

    @Override
    public String execute(String input, SocketChannel socket) throws IOException {
        try {
            parse(input);
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
        Map<String, String> info = argumentsParser.parse(input);
        if (!info.containsKey("game-id")) {
            throw new MissingArgumentsException("Game ID is required");
        }
        this.gameId = info.get("game-id");
        String playerCountArgument = info.get("number-of-players");
        if (playerCountArgument == null) {
            this.playerCount = DEFAULT_MAX_PLAYER_COUNT;
            return;
        }
        try {
            this.playerCount = Integer.parseInt(playerCountArgument);
        } catch (NumberFormatException exception) {
            throw new MissingArgumentsException("Number of players must be an integer", exception);
        }
    }
}
