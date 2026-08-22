package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.server.command.parser.ArgumentsParserImpl;
import bg.sofia.uni.fmi.mjt.server.game.GameManager;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateGameCommandTest {
    private static final String EXPECTED_SUCCESS = "Successfully created game";
    private static final String EXPECTED_MISSING_ARGS = "Need player count and game name";
    private static final String EXPECTED_NOT_LOGGED_IN = "You are not logged in";
    private static final String CREATOR = "creator";

    @Test
    void testCreatesTwoPlayerGameWhenCountIsOmitted() throws Exception {
        GameManager gameManager = Mockito.mock(GameManager.class);
        UserManager userManager = Mockito.mock(UserManager.class);
        SocketChannel channel = Mockito.mock(SocketChannel.class);
        when(userManager.getUsername(channel)).thenReturn(CREATOR);

        String response = command(gameManager, userManager)
                .execute("create-game --game-id=game1", channel);

        assertEquals(EXPECTED_SUCCESS, response);
        verify(gameManager).createGame("game1", CREATOR, 2);
    }

    @Test
    void testCreatesGameWithRequestedPlayerCount() throws Exception {
        GameManager gameManager = Mockito.mock(GameManager.class);
        UserManager userManager = Mockito.mock(UserManager.class);
        SocketChannel channel = Mockito.mock(SocketChannel.class);
        when(userManager.getUsername(channel)).thenReturn(CREATOR);

        String response = command(gameManager, userManager)
                .execute(createCommand("game1", "4"), channel);

        assertEquals(EXPECTED_SUCCESS, response);
        verify(gameManager).createGame("game1", CREATOR, 4);
    }

    @Test
    void testRejectsMissingGameIdWithoutUsingManagers() throws Exception {
        GameManager gameManager = Mockito.mock(GameManager.class);
        UserManager userManager = Mockito.mock(UserManager.class);

        String response = command(gameManager, userManager)
                .execute("create-game --number-of-players=2", Mockito.mock(SocketChannel.class));

        assertEquals(EXPECTED_MISSING_ARGS, response);
        verify(gameManager, never()).createGame(any(), any(), any(Integer.class));
    }

    @Test
    void testRejectsNonNumericPlayerCountWithoutCreatingGame() throws Exception {
        GameManager gameManager = Mockito.mock(GameManager.class);
        UserManager userManager = Mockito.mock(UserManager.class);

        String response = command(gameManager, userManager)
                .execute(createCommand("game1", "three"), Mockito.mock(SocketChannel.class));

        assertEquals(EXPECTED_MISSING_ARGS, response);
        verify(gameManager, never()).createGame(any(), any(), any(Integer.class));
    }

    @Test
    void testConvertsNotLoggedInExceptionToClientResponse() throws Exception {
        GameManager gameManager = Mockito.mock(GameManager.class);
        UserManager userManager = Mockito.mock(UserManager.class);
        SocketChannel channel = Mockito.mock(SocketChannel.class);
        when(userManager.getUsername(channel)).thenThrow(new UserNotLoggedInException("Not logged in"));

        String response = command(gameManager, userManager)
                .execute(createCommand("game1"), channel);

        assertEquals(EXPECTED_NOT_LOGGED_IN, response);
        verify(gameManager, never()).createGame(any(), any(), any(Integer.class));
    }

    private CreateGameCommand command(GameManager gameManager, UserManager userManager) {
        return new CreateGameCommand(gameManager, userManager, new ArgumentsParserImpl());
    }

    private String createCommand(String gameid) {
        return "create-game --game-id=" + gameid;
    }
    private String createCommand(String gameid, String playerCount) {
        return  "create-game --game-id=" + gameid + " --number-of-players=" + playerCount;
    }
}
