package bg.sofia.uni.fmi.mjt.server.serverCommand;

import bg.sofia.uni.fmi.mjt.command.serverCommand.ListGamesCommand;
import bg.sofia.uni.fmi.mjt.command.serverCommand.parser.ArgumentsParserImpl;
import bg.sofia.uni.fmi.mjt.server.game.GameInfo;
import bg.sofia.uni.fmi.mjt.server.game.GameManager;
import bg.sofia.uni.fmi.mjt.server.game.GameStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ListGamesCommandTest {
    private static final String EXPECTED_GAMES_HEADER = "Games:" + System.lineSeparator();
    private static final String EXPECTED_INVALID_STATUS = "Need status(All, Available, Ended, Started)";

    private GameManager gameManager;
    private SocketChannel socket;

    @BeforeEach
    void setUp() {
        gameManager = Mockito.mock(GameManager.class);
        socket = Mockito.mock(SocketChannel.class);
    }

    @Test
    void testListsAllGamesWhenStatusMissing() throws Exception {
        GameInfo availableGame = game("available", GameStatus.AVAILABLE);
        GameInfo startedGame = game("started", GameStatus.STARTED);
        when(gameManager.listGames()).thenReturn(List.of(availableGame, startedGame));

        String response = command().execute(createCommand(), socket);

        assertEquals(EXPECTED_GAMES_HEADER
                + availableGame.getGameInfo() + System.lineSeparator()
                + startedGame.getGameInfo() + System.lineSeparator(), response);
    }

    @Test
    void testListsOnlyGamesWithRequestedStatusIgnoringCase() throws Exception {
        GameInfo availableGame = game("available", GameStatus.AVAILABLE);
        GameInfo endedGame = game("ended", GameStatus.ENDED);
        when(gameManager.listGames()).thenReturn(List.of(availableGame, endedGame));

        String response = command().execute(createCommand("available"), socket);

        assertEquals(EXPECTED_GAMES_HEADER + availableGame.getGameInfo() + System.lineSeparator(), response);
    }

    @Test
    void testListsGamesForEveryStatusFilter() throws Exception {
        List<GameInfo> games = List.of(
                game("available", GameStatus.AVAILABLE),
                game("started", GameStatus.STARTED),
                game("ended", GameStatus.ENDED)
        );
        when(gameManager.listGames()).thenReturn(games);

        for (GameStatus status : GameStatus.values()) {
            StringBuilder expected = new StringBuilder(EXPECTED_GAMES_HEADER);
            for (GameInfo game : games) {
                if (status == GameStatus.ALL || game.status() == status) {
                    expected.append(game.getGameInfo()).append(System.lineSeparator());
                }
            }

            assertEquals(expected.toString(),
                    command().execute(createCommand(status.name()), socket));
        }
    }

    @Test
    void testReturnsHeaderWhenThereAreNoGames() throws Exception {
        when(gameManager.listGames()).thenReturn(List.of());

        assertEquals(EXPECTED_GAMES_HEADER, command().execute(createCommand(), socket));
    }

    @Test
    void testRejectsUnknownStatus() throws Exception {
        String response = command().execute(createCommand("unknown"), socket);

        assertEquals(EXPECTED_INVALID_STATUS, response);
    }

    private ListGamesCommand command() {
        return new ListGamesCommand(gameManager, new ArgumentsParserImpl());
    }

    private GameInfo game(String gameId, GameStatus status) {
        return new GameInfo(gameId, "game", status, List.of(1), 2);
    }

    private String createCommand() {
        return "list-games";
    }
    private String createCommand(String status) {
        return "list-games --status=" + status;
    }
}
