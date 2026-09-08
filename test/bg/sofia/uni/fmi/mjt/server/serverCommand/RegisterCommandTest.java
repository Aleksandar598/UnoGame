package bg.sofia.uni.fmi.mjt.server.serverCommand;

import bg.sofia.uni.fmi.mjt.command.serverCommand.RegisterCommand;
import bg.sofia.uni.fmi.mjt.exception.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.command.serverCommand.parser.ArgumentsParserImpl;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegisterCommandTest {
    private static final String EXPECTED_SUCCESSFUL_REGISTRATION = "Registration is successful";
    private static final String EXPECTED_MISSING_ARGS = "Need username and password";
    private static final String EXPECTED_EXISTING_USER = "User with this name already exists";

    private UserManager userManager;
    private SocketChannel socket;

    @BeforeEach
    void setUp() {
        userManager = Mockito.mock(UserManager.class);
        socket = Mockito.mock(SocketChannel.class);
    }

    @Test
    void testRegistersParsedCredentials() throws Exception {
        String response = command().execute(createCommand("user", "password"), socket);

        assertEquals(EXPECTED_SUCCESSFUL_REGISTRATION, response);
        verify(userManager).register("user", "password");
    }

    @Test
    void testRejectsMissingPasswordCredentialsWithoutRegisteringUser() throws Exception {
        String response = command().execute("register --username=user", socket);

        assertEquals(EXPECTED_MISSING_ARGS, response);
        verify(userManager, never()).register(any(), any());
    }

    @Test
    void testRejectsBlankCredentialsWithoutRegisteringUser() throws Exception {
        String response = command().execute(createCommand("", ""), socket);

        assertEquals(EXPECTED_MISSING_ARGS, response);
        verify(userManager, never()).register(any(), any());
    }

    @Test
    void testConvertsDuplicateUserExceptionToClientResponse() throws Exception {
        when(userManager.register("user", "password"))
                .thenThrow(new UserAlreadyExistsException("Already exists"));

        String response = command().execute(createCommand("user", "password"), socket);

        assertEquals(EXPECTED_EXISTING_USER, response);
    }

    private RegisterCommand command() {
        return new RegisterCommand(userManager, new ArgumentsParserImpl());
    }

    private String createCommand(String username, String password) {
        return "register --username=" + username + " --password=" + password;
    }
}
