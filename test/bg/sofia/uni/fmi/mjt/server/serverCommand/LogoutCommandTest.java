package bg.sofia.uni.fmi.mjt.server.serverCommand;

import bg.sofia.uni.fmi.mjt.command.serverCommand.LogoutCommand;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.server.game.GameManager;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LogoutCommandTest {
    private static final String EXPECTED_SUCCESSFUL_LOGOUT = "Logout Successful";
    private static final String EXPECTED_NOT_LOGGED_IN = "Not logged in";
    private static final String EXPECTED_UNEXPECTED_ERROR = "Unexpected error occurred";

    private UserManager userManager;
    private GameManager gameManager;
    private SocketChannel socket;

    @BeforeEach
    void setUp() {
        userManager = Mockito.mock(UserManager.class);
        gameManager = Mockito.mock(GameManager.class);
        socket = Mockito.mock(SocketChannel.class);
        when(userManager.isLoggedIn(socket)).thenReturn(true);
    }

    @Test
    void testLogsOutPlayerWhoIsNotInAGame() throws Exception {
        Player player = Mockito.mock(Player.class);
        when(userManager.getPlayer(socket)).thenReturn(player);
        when(player.getId()).thenReturn(7);
        when(gameManager.isInGame(7)).thenReturn(false);

        String response = command().execute("logout", socket);

        assertEquals(EXPECTED_SUCCESSFUL_LOGOUT, response);
        verify(userManager).logout(socket);
        verify(gameManager, never()).leaveGame(player);
    }

    @Test
    void testLeavesGameBeforeLoggingOutPlayerInGame() throws Exception {
        Player player = Mockito.mock(Player.class);
        when(userManager.getPlayer(socket)).thenReturn(player);
        when(player.getId()).thenReturn(7);
        when(gameManager.isInGame(7)).thenReturn(true);

        String response = command().execute("logout", socket);

        assertEquals(EXPECTED_SUCCESSFUL_LOGOUT, response);
        verify(gameManager).leaveGame(player);
        verify(userManager).logout(socket);
    }

    @Test
    void testReturnsNotLoggedInWhenSocketHasNoSession() throws Exception {
        when(userManager.isLoggedIn(socket)).thenReturn(false);

        String response = command().execute("logout", socket);

        assertEquals(EXPECTED_NOT_LOGGED_IN, response);
        verify(userManager, never()).logout(socket);
        verify(userManager, never()).getPlayer(socket);
    }

    @Test
    void testLogsOutSessionWithoutGamePlayer() throws Exception {
        when(userManager.getPlayer(socket)).thenThrow(new UnoUserException("User not created"));

        assertEquals(EXPECTED_SUCCESSFUL_LOGOUT, command().execute("logout", socket));

        verify(userManager).logout(socket);
        Mockito.verifyNoInteractions(gameManager);
    }

    @Test
    void testReturnsNotLoggedInWhenSessionExpiresBeforePlayerLookup() throws Exception {
        when(userManager.getPlayer(socket))
                .thenThrow(new UserNotLoggedInException(EXPECTED_NOT_LOGGED_IN));

        assertEquals(EXPECTED_NOT_LOGGED_IN, command().execute("logout", socket));
        verify(userManager, never()).logout(socket);
    }

    @Test
    void testReturnsUnexpectedErrorWhenLeavingGameFails() throws Exception {
        Player player = Mockito.mock(Player.class);
        when(userManager.getPlayer(socket)).thenReturn(player);
        when(player.getId()).thenReturn(7);
        when(gameManager.isInGame(7)).thenReturn(true);
        doThrow(new PlayerNotFoundException("Player not found"))
                .when(gameManager).leaveGame(player);

        String response = command().execute("logout", socket);

        assertEquals(EXPECTED_UNEXPECTED_ERROR, response);
        verify(userManager, never()).logout(socket);
    }

    @Test
    void testReturnsUnexpectedErrorWhenUserCannotLeaveGame() throws Exception {
        Player player = Mockito.mock(Player.class);
        when(userManager.getPlayer(socket)).thenReturn(player);
        when(player.getId()).thenReturn(7);
        when(gameManager.isInGame(7)).thenReturn(true);
        doThrow(new UnoUserException("Player is not in a game"))
                .when(gameManager).leaveGame(player);

        String response = command().execute("logout", socket);

        assertEquals(EXPECTED_UNEXPECTED_ERROR, response);
        verify(userManager, never()).logout(socket);
    }

    private LogoutCommand command() {
        return new LogoutCommand(userManager, gameManager);
    }
}
