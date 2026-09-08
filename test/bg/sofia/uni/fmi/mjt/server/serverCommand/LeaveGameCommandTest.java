package bg.sofia.uni.fmi.mjt.server.serverCommand;

import bg.sofia.uni.fmi.mjt.command.serverCommand.LeaveGameCommand;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.server.game.GameManager;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;

class LeaveGameCommandTest {
    private static final String EXPECTED_SUCCESSFUL_LEAVE = "left the game";
    private static final String EXPECTED_NOT_LOGGED_IN = "User is not logged in";
    private static final String EXPECTED_PLAYER_NOT_FOUND = "Player not found";
    private static final String EXPECTED_USER_ERROR = "Player is not in a game";
    private static final String PLAYER_NAME = "Player";

    private UserManager userManager;
    private GameManager gameManager;
    private SocketChannel socket;

    @BeforeEach
    void setUp() {
        userManager = Mockito.mock(UserManager.class);
        gameManager = Mockito.mock(GameManager.class);
        socket = Mockito.mock(SocketChannel.class);
    }

    @Test
    void testLeavesGameAndNotifiesParticipants() throws Exception {
        Player player = Mockito.mock(Player.class);
        when(userManager.getPlayer(socket)).thenReturn(player);
        when(player.getName()).thenReturn(PLAYER_NAME);

        String response = command().execute("leave", socket);

        assertEquals(EXPECTED_SUCCESSFUL_LEAVE, response);
        verify(gameManager).leaveGame(player);
        verify(gameManager).notifyAllInAGame(player, PLAYER_NAME + EXPECTED_SUCCESSFUL_LEAVE);
    }

    @Test
    void testReturnsNotLoggedInWhenSocketHasNoSession() throws Exception {
        when(userManager.getPlayer(socket))
                .thenThrow(new UserNotLoggedInException(EXPECTED_NOT_LOGGED_IN));

        String response = command().execute("leave", socket);

        assertEquals(EXPECTED_NOT_LOGGED_IN, response);
        verify(gameManager, never()).leaveGame(Mockito.any());
    }

    @Test
    void testReturnsPlayerNotFoundMessageWhenManagerCannotRemovePlayer() throws Exception {
        Player player = Mockito.mock(Player.class);
        when(userManager.getPlayer(socket)).thenReturn(player);
        doThrow(new PlayerNotFoundException(EXPECTED_PLAYER_NOT_FOUND))
                .when(gameManager).leaveGame(player);

        String response = command().execute("leave", socket);

        assertEquals(EXPECTED_PLAYER_NOT_FOUND, response);
        verify(gameManager, never()).notifyAllInAGame(Mockito.any(), Mockito.anyString());
    }

    @Test
    void testReturnsUnoUserMessageWhenPlayerIsNotInGame() throws Exception {
        Player player = Mockito.mock(Player.class);
        when(userManager.getPlayer(socket)).thenReturn(player);
        doThrow(new UnoUserException(EXPECTED_USER_ERROR))
                .when(gameManager).leaveGame(player);

        String response = command().execute("leave", socket);

        assertEquals(EXPECTED_USER_ERROR, response);
        verify(gameManager, never()).notifyAllInAGame(Mockito.any(), Mockito.anyString());
    }

    private LeaveGameCommand command() {
        return new LeaveGameCommand(userManager, gameManager);
    }
}
