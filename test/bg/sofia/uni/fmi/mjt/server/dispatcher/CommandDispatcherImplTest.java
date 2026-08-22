package bg.sofia.uni.fmi.mjt.server.dispatcher;

import bg.sofia.uni.fmi.mjt.server.command.Command;
import bg.sofia.uni.fmi.mjt.server.exception.UnknownCommandException;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CommandDispatcherImplTest {
    private static final String INPUT_LOGIN = "login --username=user --password=password";
    private static final String INPUT_LIST_GAMES = "list-games";
    private static final String INPUT_GAME_COMMAND = "show-hand";
    private static final String EXPECTED_BLANK_COMMAND = "Command cannot be blank";
    private static final String EXPECTED_NOT_LOGGED_IN = "You are not logged in";
    private static final String EXPECTED_SERVER_COMMAND_RESPONSE = "Login successful";
    private static final String EXPECTED_GAME_COMMAND_RESPONSE = "Your hand";
    private static final String EXPECTED_CANNOT_PROCESS_COMMAND = "Could not process command";

    private UserManager userManager;
    private ServerCommandCreator serverCommandCreator;
    private Command serverCommand;
    private Command inGameCommand;
    private SocketChannel socket;
    private CommandDispatcherImpl dispatcher;

    @BeforeEach
    void setUp() {
        userManager = Mockito.mock(UserManager.class);
        serverCommandCreator = Mockito.mock(ServerCommandCreator.class);
        serverCommand = Mockito.mock(Command.class);
        inGameCommand = Mockito.mock(Command.class);
        socket = Mockito.mock(SocketChannel.class);
        dispatcher = new CommandDispatcherImpl(userManager, serverCommandCreator, inGameCommand);

    }

    @Test
    void testRejectsNullOrBlankInput() {
        assertEquals(EXPECTED_BLANK_COMMAND, dispatcher.dispatch(null, socket));
        assertEquals(EXPECTED_BLANK_COMMAND, dispatcher.dispatch("   ", socket));
        verifyNoInteractions(userManager, serverCommandCreator, inGameCommand);
    }

    @Test
    void testAllowsLoginWithoutCheckingLoggedInStatus() throws Exception {
        when(serverCommandCreator.create(INPUT_LOGIN)).thenReturn(serverCommand);
        when(serverCommand.execute(INPUT_LOGIN, socket)).thenReturn(EXPECTED_SERVER_COMMAND_RESPONSE);

        String response = dispatcher.dispatch(INPUT_LOGIN, socket);

        assertEquals(EXPECTED_SERVER_COMMAND_RESPONSE, response);
        verify(userManager, never()).isLoggedIn(socket);
        verify(serverCommand).execute(INPUT_LOGIN, socket);
    }

    @Test
    void testRejectsProtectedCommandForUserWhoIsNotLoggedIn() {
        when(userManager.isLoggedIn(socket)).thenReturn(false);

        String response = dispatcher.dispatch(INPUT_LIST_GAMES, socket);

        assertEquals(EXPECTED_NOT_LOGGED_IN, response);
        verifyNoInteractions(serverCommandCreator, inGameCommand);
    }

    @Test
    void testDispatchesKnownServerCommandForLoggedInUser() throws Exception {
        when(userManager.isLoggedIn(socket)).thenReturn(true);
        when(serverCommandCreator.create(INPUT_LIST_GAMES)).thenReturn(serverCommand);
        when(serverCommand.execute(INPUT_LIST_GAMES, socket)).thenReturn(EXPECTED_SERVER_COMMAND_RESPONSE);

        String response = dispatcher.dispatch(INPUT_LIST_GAMES, socket);

        assertEquals(EXPECTED_SERVER_COMMAND_RESPONSE, response);
        verify(serverCommand).execute(INPUT_LIST_GAMES, socket);
        verifyNoInteractions(inGameCommand);
    }

    @Test
    void testDispatchesUnknownServerCommandAsGameCommand() throws Exception {
        when(userManager.isLoggedIn(socket)).thenReturn(true);
        when(serverCommandCreator.create(INPUT_GAME_COMMAND))
                .thenThrow(new UnknownCommandException("Unknown command"));
        when(inGameCommand.execute(INPUT_GAME_COMMAND, socket)).thenReturn(EXPECTED_GAME_COMMAND_RESPONSE);

        String response = dispatcher.dispatch(INPUT_GAME_COMMAND, socket);

        assertEquals(EXPECTED_GAME_COMMAND_RESPONSE, response);
        verify(inGameCommand).execute(INPUT_GAME_COMMAND, socket);
    }

    @Test
    void testReturnsProcessingErrorWhenServerCommandThrowsIOException() throws Exception {
        when(userManager.isLoggedIn(socket)).thenReturn(true);
        when(serverCommandCreator.create(INPUT_LIST_GAMES)).thenReturn(serverCommand);
        when(serverCommand.execute(INPUT_LIST_GAMES, socket)).thenThrow(new IOException());

        String response = dispatcher.dispatch(INPUT_LIST_GAMES, socket);

        assertEquals(EXPECTED_CANNOT_PROCESS_COMMAND, response);
    }

    @Test
    void testReturnsProcessingErrorWhenGameCommandThrowsIOException() throws Exception {
        when(userManager.isLoggedIn(socket)).thenReturn(true);
        when(serverCommandCreator.create(INPUT_GAME_COMMAND))
                .thenThrow(new UnknownCommandException("Unknown command"));
        when(inGameCommand.execute(INPUT_GAME_COMMAND, socket)).thenThrow(new IOException());

        String response = dispatcher.dispatch(INPUT_GAME_COMMAND, socket);

        assertEquals(EXPECTED_CANNOT_PROCESS_COMMAND, response);
    }


}
