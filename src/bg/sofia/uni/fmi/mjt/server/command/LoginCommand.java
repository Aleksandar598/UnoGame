package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.exception.WrongUserCredentialsException;
import bg.sofia.uni.fmi.mjt.server.command.parser.ArgumentsParser;
import bg.sofia.uni.fmi.mjt.server.command.parser.ArgumentsParserImpl;
import bg.sofia.uni.fmi.mjt.server.exception.MissingArgumentsException;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import bg.sofia.uni.fmi.mjt.server.user.UserManagerImpl;

import java.nio.channels.SocketChannel;
import java.util.Map;

public class LoginCommand extends AbstractCommand {

    ArgumentsParser argumentsParser = new ArgumentsParserImpl();
    private String username;
    private String password;

    @Override
    public void execute(String input, SocketChannel socket) throws MissingArgumentsException, WrongUserCredentialsException {
        parse(input);
        UserManager userManager = UserManagerImpl.getInstance();
        if (userManager.isLoggedIn(socket)) {
            throw new IllegalStateException("User is already logged in");
        }
        userManager.login(this.username, this.password, socket);
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
