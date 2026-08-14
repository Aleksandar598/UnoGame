package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.exception.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.exception.WrongUserCredentialsException;
import bg.sofia.uni.fmi.mjt.server.exception.ExceptionLogger;
import bg.sofia.uni.fmi.mjt.server.exception.MissingArgumentsException;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import bg.sofia.uni.fmi.mjt.server.user.UserManagerImpl;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Map;

public class RegisterCommand extends AbstractCommand {

    private String username;
    private String password;
    private static final String MISSING_ARGS = "Need username and password";
    private static final String EXISTING_USER = "User with this name already exists";
    private static final String SUCCESS = "Registration is successful";

    @Override
    public String execute(String input, SocketChannel socket) throws IOException {
        try {
            parse(input);
            UserManager userManager = UserManagerImpl.getInstance();
            userManager.register(this.username, this.password);
        } catch (MissingArgumentsException e) {
            ExceptionLogger.logException(e);
            return MISSING_ARGS;
        } catch (UserAlreadyExistsException e) {
            ExceptionLogger.logException(e);
            return EXISTING_USER;
        }
        return SUCCESS;
    }

    private void parse(String input) throws MissingArgumentsException {
        Map<String, String> arguments = parser.parse(input);
        if (!arguments.containsKey("username") || !arguments.containsKey("password")) {
            throw new MissingArgumentsException("Registration requires username and password");
        }
        this.username = arguments.get("username");
        this.password = arguments.get("password");
    }
}
