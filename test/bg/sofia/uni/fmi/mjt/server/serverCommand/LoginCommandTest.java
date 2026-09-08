package bg.sofia.uni.fmi.mjt.server.serverCommand;

import bg.sofia.uni.fmi.mjt.command.serverCommand.LoginCommand;
import bg.sofia.uni.fmi.mjt.exception.WrongUserCredentialsException;
import bg.sofia.uni.fmi.mjt.command.serverCommand.parser.ArgumentsParserImpl;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginCommandTest {

    private static final String EXPECTED_SUCCESS = "Login Successful";
    private static final String EXPECTED_FAILURE = "Login Failed";
    private static final String EXPECTED_MISSING_ARGS = "Need both username and password";
    private static final String EXPECTED_WRONG_CREDENTIALS = "Wrong user credentials";

    @Test
    void testDelegatesParsedCredentialsToUserManager() throws Exception {
        UserManager manager = Mockito.mock(UserManager.class);
        SocketChannel channel = Mockito.mock(SocketChannel.class);
        when(manager.isLoggedIn(channel)).thenReturn(false);

        String response = command(manager).execute(createCommand("user", "password"), channel);

        assertEquals(EXPECTED_SUCCESS, response);
        verify(manager).login("user", "password", channel);
    }

    @Test
    void testsRejectsMissingArgumentsWithoutCallingUserManager() throws Exception {
        UserManager manager = Mockito.mock(UserManager.class);

        String response = command(manager).execute("login --username=user", Mockito.mock(SocketChannel.class));

        assertEquals(EXPECTED_MISSING_ARGS , response);
        verify(manager, never()).login(any(), any(), any());
    }

    @Test
    void testConvertsWrongCredentialsExceptionToClientResponse() throws Exception {
        UserManager manager = Mockito.mock(UserManager.class);
        SocketChannel channel = Mockito.mock(SocketChannel.class);
        when(manager.isLoggedIn(channel)).thenReturn(false);
        when(manager.login("user", "wrong", channel))
                .thenThrow(new WrongUserCredentialsException("Wrong credentials"));

        assertEquals(EXPECTED_WRONG_CREDENTIALS,
                command(manager).execute(createCommand("user", "wrong"), channel));
    }

    @Test
    void testDoesNotLoginAnAlreadyLoggedInSocket() throws Exception {
        UserManager manager = Mockito.mock(UserManager.class);
        SocketChannel channel = Mockito.mock(SocketChannel.class);
        when(manager.isLoggedIn(channel)).thenReturn(true);

        assertEquals(EXPECTED_FAILURE,
                command(manager).execute(createCommand("user", "password"), channel));
        verify(manager, never()).login(any(), any(), any());
    }

    private LoginCommand command(UserManager manager) {
        return new LoginCommand(manager, new ArgumentsParserImpl());
    }

    private String createCommand(String name, String password) {
        return "login --username=" + name + " --password=" + password;
    }
}
