package bg.sofia.uni.fmi.mjt.server.command.parser;

import bg.sofia.uni.fmi.mjt.exception.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.exception.WrongUserCredentialsException;
import bg.sofia.uni.fmi.mjt.server.command.AbstractCommand;
import bg.sofia.uni.fmi.mjt.server.exception.MissingArgumentsException;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import bg.sofia.uni.fmi.mjt.server.user.UserManagerImpl;

import java.nio.channels.SocketChannel;
import java.util.Map;

public class RegisterCommand extends AbstractCommand {

    private String username;
    private String password;

    @Override
    public void execute(String input, SocketChannel socket) throws MissingArgumentsException, WrongUserCredentialsException, UserAlreadyExistsException {
        parse(input);
        UserManager userManager = UserManagerImpl.getInstance();
        userManager.register(this.username, this.password);
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
