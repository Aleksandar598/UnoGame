package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.server.exception.ExceptionLogger;
import bg.sofia.uni.fmi.mjt.server.exception.MissingArgumentsException;
import bg.sofia.uni.fmi.mjt.server.game.GameManager;
import bg.sofia.uni.fmi.mjt.server.game.GameManagerImpl;
import bg.sofia.uni.fmi.mjt.server.command.parser.ArgumentsParser;
import bg.sofia.uni.fmi.mjt.server.command.parser.ArgumentsParserImpl;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Map;

public class SummaryCommand extends AbstractCommand {
    private String gameId;
    private final GameManager gameManager;
    private final ArgumentsParser argumentsParser;

    public SummaryCommand() {
        this(GameManagerImpl.getInstance(), new ArgumentsParserImpl());
    }

    SummaryCommand(GameManager gameManager, ArgumentsParser argumentsParser) {
        if (gameManager == null || argumentsParser == null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }
        this.gameManager = gameManager;
        this.argumentsParser = argumentsParser;
    }

    @Override
    public String execute(String input, SocketChannel socket) throws IOException {
        try {
            parse(input);
            return gameManager.getEndedGameInfo(gameId);
        } catch (MissingArgumentsException e) {
            ExceptionLogger.logException(e);
            return e.getMessage();
        }
    }

    private void parse(String input) throws MissingArgumentsException {
        Map<String, String> args = argumentsParser.parse(input);
        if (!args.containsKey("game-id")) {
            throw new MissingArgumentsException("Must select gameid");
        }
        this.gameId = args.get("game-id");
    }
}
