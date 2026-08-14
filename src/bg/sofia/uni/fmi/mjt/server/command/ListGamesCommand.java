package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.server.exception.ExceptionLogger;
import bg.sofia.uni.fmi.mjt.server.exception.MissingArgumentsException;
import bg.sofia.uni.fmi.mjt.server.game.GameInfo;
import bg.sofia.uni.fmi.mjt.server.game.GameManager;
import bg.sofia.uni.fmi.mjt.server.game.GameManagerImpl;
import bg.sofia.uni.fmi.mjt.server.game.GameStatus;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;

public class ListGamesCommand extends AbstractCommand {

    GameStatus status;

    private static final String MISSING_ARGS = "Need status(All, Available, Ended, Started)";

    @Override
    public String execute(String input, SocketChannel socket) throws IOException {
        try {
            this.parse(input);
            GameManager manager = GameManagerImpl.getInstance();
            StringBuilder sb = new StringBuilder();
            List<GameInfo> games = manager.listGames();
            for (GameInfo game: games){
                if (game.status().equals(this.status)){
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
        Map<String, String> map = parser.parse(input);
        if (!map.containsKey("status")) {
            throw new MissingArgumentsException("Status is required");
        }
        try {
            this.status = GameStatus.valueOf(map.get("status"));
        } catch (IllegalArgumentException e) {
            throw new MissingArgumentsException(e.getMessage());
        }
    }
}
