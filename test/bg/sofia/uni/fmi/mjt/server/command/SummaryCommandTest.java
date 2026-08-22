package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.server.command.parser.ArgumentsParserImpl;
import bg.sofia.uni.fmi.mjt.server.game.GameManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SummaryCommandTest {
    private static final String EXPECTED_ENDED_GAME_SUMMARY = "Game ID: game" + System.lineSeparator() +"Winners: user";
    private static final String EXPECTED_MISSING_GAME_ID = "Must select gameid";

    private GameManager gameManager;
    private SocketChannel socket;

    @BeforeEach
    void setUp() {
        gameManager = Mockito.mock(GameManager.class);
        socket = Mockito.mock(SocketChannel.class);
    }

    @Test
    void testReturnsEndedGameSummaryForRequestedGame() throws Exception {
        when(gameManager.getEndedGameInfo("game")).thenReturn(EXPECTED_ENDED_GAME_SUMMARY);

        String response = command().execute("summary --game-id=game", socket);

        assertEquals(EXPECTED_ENDED_GAME_SUMMARY, response);
        verify(gameManager).getEndedGameInfo("game");
    }

    @Test
    void testRejectsMissingGameIdWithoutQueryingGameManager() throws Exception {
        String response = command().execute("summary", socket);

        assertEquals(EXPECTED_MISSING_GAME_ID, response);
        verify(gameManager, never()).getEndedGameInfo(Mockito.anyString());
    }

    private SummaryCommand command() {
        return new SummaryCommand(gameManager, new ArgumentsParserImpl());
    }
}
