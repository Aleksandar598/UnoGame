package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.exception.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.server.exception.ExceptionLogger;
import bg.sofia.uni.fmi.mjt.server.exception.MissingArgumentsException;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import bg.sofia.uni.fmi.mjt.server.user.UserManagerImpl;
import bg.sofia.uni.fmi.mjt.server.command.parser.ArgumentsParser;
import bg.sofia.uni.fmi.mjt.server.command.parser.ArgumentsParserImpl;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Map;

public class RegisterCommand extends AbstractCommand {

    private String username;
    private String password;
    private final UserManager userManager;
    private final ArgumentsParser argumentsParser;
    private static final String MISSING_ARGS = "Need username and password";
    private static final String EXISTING_USER = "User with this name already exists";
    private static final String SUCCESS = "Registration is successful";

    public RegisterCommand() {
        this(UserManagerImpl.getInstance(), new ArgumentsParserImpl());
    }

    RegisterCommand(UserManager userManager, ArgumentsParser argumentsParser) {
        if (userManager == null || argumentsParser == null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }
        this.userManager = userManager;
        this.argumentsParser = argumentsParser;
    }

    @Override
    public String execute(String input, SocketChannel socket) throws IOException {
        try {
            parse(input);
            if (this.username.isBlank() || this.password.isBlank()) {
                throw new MissingArgumentsException("Username or password is blank");
            }

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
        Map<String, String> arguments = argumentsParser.parse(input);
        if (!arguments.containsKey("username") || !arguments.containsKey("password")) {
            throw new MissingArgumentsException("Registration requires username and password");
        }
        this.username = arguments.get("username");
        this.password = arguments.get("password");
    }
}
