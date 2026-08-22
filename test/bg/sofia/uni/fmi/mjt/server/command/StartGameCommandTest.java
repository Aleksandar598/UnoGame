package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.exception.CannotStartGameException;
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

class StartGameCommandTest {
    private static final String EXPECTED_SUCCESSFUL_START = null;
    private static final String EXPECTED_NOT_LOGGED_IN = "User is not logged in";
    private static final String EXPECTED_USER_NOT_CREATED = "User has not been created";
    private static final String EXPECTED_NOT_JOINED_GAME = "User has not joined game";
    private static final String EXPECTED_CANNOT_START_GAME = "Cannot start game";
    private static final String EXPECTED_GAME_STARTED_NOTIFICATION = "Game has started!";
    private static final String USERNAME = "user";

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
    void testStartsGameAndNotifiesParticipants() throws Exception {
        Player player = Mockito.mock(Player.class);
        when(userManager.getPlayer(socket)).thenReturn(player);
        when(userManager.getUsername(socket)).thenReturn(USERNAME);

        String response = command().execute("start", socket);

        assertEquals(EXPECTED_SUCCESSFUL_START, response);
        verify(gameManager).startGame(player, USERNAME);
        verify(gameManager).notifyAllInAGame(player, EXPECTED_GAME_STARTED_NOTIFICATION);
    }

    @Test
    void testReturnsNotLoggedInWhenSocketHasNoSession() throws Exception {
        when(userManager.getPlayer(socket))
                .thenThrow(new UserNotLoggedInException(EXPECTED_NOT_LOGGED_IN));

        assertEquals(EXPECTED_NOT_LOGGED_IN, command().execute("start", socket));
        verify(gameManager, never()).startGame(Mockito.any(), Mockito.anyString());
    }

    @Test
    void testReturnsUserNotCreatedWhenSocketHasNoPlayer() throws Exception {
        when(userManager.getPlayer(socket))
                .thenThrow(new UnoUserException(EXPECTED_USER_NOT_CREATED));

        assertEquals(EXPECTED_USER_NOT_CREATED, command().execute("start", socket));
    }

    @Test
    void testReturnsNotJoinedWhenManagerCannotFindPlayer() throws Exception {
        Player player = Mockito.mock(Player.class);
        when(userManager.getPlayer(socket)).thenReturn(player);
        when(userManager.getUsername(socket)).thenReturn(USERNAME);
        doThrow(new PlayerNotFoundException("Player not found"))
                .when(gameManager).startGame(player, USERNAME);

        assertEquals(EXPECTED_NOT_JOINED_GAME, command().execute("start", socket));
    }

    @Test
    void testReturnsCannotStartWhenGameHasTooFewPlayers() throws Exception {
        Player player = Mockito.mock(Player.class);
        when(userManager.getPlayer(socket)).thenReturn(player);
        when(userManager.getUsername(socket)).thenReturn(USERNAME);
        doThrow(new CannotStartGameException("Not enough players"))
                .when(gameManager).startGame(player, USERNAME);

        assertEquals(EXPECTED_CANNOT_START_GAME, command().execute("start", socket));
    }

    private StartGameCommand command() {
        return new StartGameCommand(userManager, gameManager);
    }
}
