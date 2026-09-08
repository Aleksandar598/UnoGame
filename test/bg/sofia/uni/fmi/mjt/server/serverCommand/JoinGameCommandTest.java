package bg.sofia.uni.fmi.mjt.server.serverCommand;

import bg.sofia.uni.fmi.mjt.command.serverCommand.JoinGameCommand;
import bg.sofia.uni.fmi.mjt.exception.MaximumPlayerCountReached;
import bg.sofia.uni.fmi.mjt.exception.GameHasStartedException;
import bg.sofia.uni.fmi.mjt.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.command.serverCommand.parser.ArgumentsParserImpl;
import bg.sofia.uni.fmi.mjt.server.exception.GameNotFoundException;
import bg.sofia.uni.fmi.mjt.server.game.GameManager;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JoinGameCommandTest {
    private static final String EXPECTED_SUCCESSFUL_JOIN = "successfully joined game";
    private static final String EXPECTED_INVALID_ARGS = "Invalid arguments";
    private static final String EXPECTED_GAME_FULL = "Game you want to join is full";
    private static final String EXPECTED_GAME_HAS_STARTED = "Game has already started";
    private static final String EXPECTED_GAME_NOT_FOUND = "Game has not been found";
    private static final String EXPECTED_NOT_LOGGED_IN = "You are not logged in";

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
    void testJoinGameUsingProvidedDisplayName() throws Exception {
        Player player = Mockito.mock(Player.class);
        when(player.getName()).thenReturn("Player one");
        when(userManager.bindSocketToPlayer(socket, "Player one")).thenReturn(player);

        String response = command().execute(
                createCommand("game1", "Player one"), socket);

        assertEquals(EXPECTED_SUCCESSFUL_JOIN, response);
        verify(gameManager).joinGame("game1", player);
        verify(gameManager).notifyAllInAGame(player, "Player one" + EXPECTED_SUCCESSFUL_JOIN);
    }

    @Test
    void testUsesUsernameWhenDisplayNameIsNotGIven() throws Exception {
        Player player = Mockito.mock(Player.class);
        when(userManager.getUsername(socket)).thenReturn("user");
        when(userManager.bindSocketToPlayer(socket, "user")).thenReturn(player);
        when(player.getName()).thenReturn("user");

        String response = command().execute(createCommand("game1"), socket);

        assertEquals(EXPECTED_SUCCESSFUL_JOIN, response);
        verify(gameManager).joinGame("game1", player);
    }

    @Test
    void testRejectsMissingGameIdWithoutBindingPlayer() throws Exception {
        String response = command().execute("join --display-name=player", socket);

        assertEquals(EXPECTED_INVALID_ARGS, response);
        verify(userManager, never()).bindSocketToPlayer(any(), any());
    }

    @Test
    void testUnbindsPlayerWhenGameIsFull() throws Exception {
        Player player = Mockito.mock(Player.class);
        when(userManager.bindSocketToPlayer(socket, "player")).thenReturn(player);
        doThrow(new MaximumPlayerCountReached("Game is full"))
                .when(gameManager).joinGame("game", player);

        String response = command().execute(createCommand("game", "player"), socket);

        assertEquals(EXPECTED_GAME_FULL, response);
        verify(userManager).unbindPlayer(socket);
    }

    @Test
    void testUnbindsPlayerWhenGameHasAlreadyStarted() throws Exception {
        Player player = Mockito.mock(Player.class);
        when(userManager.bindSocketToPlayer(socket, "player")).thenReturn(player);
        doThrow(new GameHasStartedException("Game has started"))
                .when(gameManager).joinGame("game", player);

        String response = command().execute(createCommand("game","player"), socket);

        assertEquals(EXPECTED_GAME_HAS_STARTED, response);
        verify(userManager).unbindPlayer(socket);
    }

    @Test
    void testUnbindsPlayerWhenGameDoesNotExist() throws Exception {
        Player player = Mockito.mock(Player.class);
        when(userManager.bindSocketToPlayer(socket, "player")).thenReturn(player);
        doThrow(new GameNotFoundException("Game not found"))
                .when(gameManager).joinGame("game", player);

        String response = command().execute(createCommand("game", "player"), socket);

        assertEquals(EXPECTED_GAME_NOT_FOUND, response);
        verify(userManager).unbindPlayer(socket);
    }

    @Test
    void returnsNotLoggedInWhenPlayerCannotBeBound() throws Exception {
        when(userManager.bindSocketToPlayer(socket, "player"))
                .thenThrow(new UserNotLoggedInException("Not logged in"));

        String response = command().execute(createCommand("game", "player"), socket);

        assertEquals(EXPECTED_NOT_LOGGED_IN, response);
        verify(gameManager, never()).joinGame(any(), any());
    }

    private JoinGameCommand command() {
        return new JoinGameCommand(userManager, gameManager, new ArgumentsParserImpl());
    }

    private String createCommand(String gameId) {
        return "join --game-id=" + gameId;
    }
    private String createCommand(String gameId, String displayName) {
        return "join --game-id=" + gameId + " --display-name=" + displayName;
    }
}
