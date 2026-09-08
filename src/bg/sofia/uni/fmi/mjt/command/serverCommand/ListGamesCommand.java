package bg.sofia.uni.fmi.mjt.command.serverCommand;

import bg.sofia.uni.fmi.mjt.server.exception.ExceptionLogger;
import bg.sofia.uni.fmi.mjt.server.exception.MissingArgumentsException;
import bg.sofia.uni.fmi.mjt.server.game.GameInfo;
import bg.sofia.uni.fmi.mjt.server.game.GameManager;
import bg.sofia.uni.fmi.mjt.server.game.GameManagerImpl;
import bg.sofia.uni.fmi.mjt.server.game.GameStatus;
import bg.sofia.uni.fmi.mjt.command.serverCommand.parser.ArgumentsParser;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ListGamesCommand extends AbstractCommand {

    private GameStatus status;
    private final GameManager gameManager;


    private static final String MISSING_ARGS = "Need status(All, Available, Ended, Started)";

    public ListGamesCommand() {
        this(GameManagerImpl.getInstance(), PARSER);
    }

    ListGamesCommand(GameManager gameManager, ArgumentsParser argumentsParser) {
        if (gameManager == null || argumentsParser == null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }
        this.gameManager = gameManager;
    }

    @Override
    public String execute(String input, SocketChannel socket) throws IOException {
        try {
            this.parse(input);
            StringBuilder sb = new StringBuilder();
            sb.append("Games:" + System.lineSeparator());
            List<GameInfo> games = gameManager.listGames();
            for (GameInfo game: games) {
                if (game.status().equals(this.status) || this.status == GameStatus.ALL) {
                    sb.append(game.getGameInfo())
                            .append(System.lineSeparator());
                }
            }
            return sb.toString();

        } catch (MissingArgumentsException e) {
            ExceptionLogger.logException(e);
            return MISSING_ARGS;
        }
    }

    private void parse(String input) throws MissingArgumentsException {
        Map<String, String> map = PARSER.parse(input);
        if (!map.containsKey("status")) {
            status = GameStatus.ALL;
            return;
        }
        try {
            this.status = GameStatus.valueOf(map.get("status").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new MissingArgumentsException(e.getMessage());
        }
    }
}
