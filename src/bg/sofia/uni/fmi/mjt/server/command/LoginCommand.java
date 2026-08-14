package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.exception.WrongUserCredentialsException;
import bg.sofia.uni.fmi.mjt.server.command.parser.ArgumentsParser;
import bg.sofia.uni.fmi.mjt.server.command.parser.ArgumentsParserImpl;
import bg.sofia.uni.fmi.mjt.server.exception.ExceptionLogger;
import bg.sofia.uni.fmi.mjt.server.exception.MissingArgumentsException;
import bg.sofia.uni.fmi.mjt.server.exception.UserAlreadyLoggedInException;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import bg.sofia.uni.fmi.mjt.server.user.UserManagerImpl;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Map;

public class LoginCommand extends AbstractCommand {

    ArgumentsParser argumentsParser = new ArgumentsParserImpl();
    private String username;
    private String password;
    private static final String SUCCESS_STRING = "Login Successful";
    private static final String ALREADY_LOGGED_STRING = "Login Failed";
    private static final String WRONG_USER_CREDENTIALS_STRING = "Wrong user credentials";
    private static final String NOT_ENOUGH_PARAMETERS = "Need both username and password";
    private static final String USER_ALREADY_LOGGED_IN = "User with this profile is already logged in";

    @Override
    public String execute(String input, SocketChannel socket) throws IOException {
        try {
            parse(input);
            UserManager userManager = UserManagerImpl.getInstance();
            if (userManager.isLoggedIn(socket)) {
                return ALREADY_LOGGED_STRING;
            }
            userManager.login(this.username, this.password, socket);

        } catch (WrongUserCredentialsException e) {
            ExceptionLogger.logException(e);
            return WRONG_USER_CREDENTIALS_STRING;
        } catch (MissingArgumentsException e) {
            ExceptionLogger.logException(e);
            return NOT_ENOUGH_PARAMETERS;
        } catch (UserAlreadyLoggedInException e) {
            return USER_ALREADY_LOGGED_IN;
        }

        return SUCCESS_STRING;
    }

    private void parse(String input) throws MissingArgumentsException {
        Map<String, String> map = argumentsParser.parse(input);
        if (!map.containsKey("username") || !map.containsKey("password")) {
            throw new MissingArgumentsException("Need both username and password to login!");
        }
        this.username = map.get("username");
        this.password = map.get("password");
    }
}
