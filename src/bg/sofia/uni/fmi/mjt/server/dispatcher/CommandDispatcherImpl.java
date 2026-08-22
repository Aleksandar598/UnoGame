package bg.sofia.uni.fmi.mjt.server.dispatcher;

import bg.sofia.uni.fmi.mjt.server.command.Command;
import bg.sofia.uni.fmi.mjt.server.command.InGameCommand;
import bg.sofia.uni.fmi.mjt.server.command.factory.CommandFactory;
import bg.sofia.uni.fmi.mjt.server.exception.UnknownCommandException;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import bg.sofia.uni.fmi.mjt.server.user.UserManagerImpl;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class CommandDispatcherImpl implements CommandDispatcher {

    private static final Set<String> COMMANDS_WHICH_DO_NOT_REQUIRE_LOGIN = Set.of("login", "register");
    private final UserManager userManager;
    private final ServerCommandCreator serverCommandCreator;
    private final Command inGameCommand;

    public CommandDispatcherImpl() {
        this(UserManagerImpl.getInstance(), CommandFactory::getCommand, new InGameCommand());
    }

    CommandDispatcherImpl(UserManager userManager, ServerCommandCreator serverCommandCreator, Command inGameCommand) {
        if (userManager == null || serverCommandCreator == null || inGameCommand == null) {
            throw new IllegalArgumentException("Dispatcher dependencies cannot be null");
        }
        this.userManager = userManager;
        this.serverCommandCreator = serverCommandCreator;
        this.inGameCommand = inGameCommand;
    }

    @Override
    public String dispatch(String input, SocketChannel socket) {
        if (input == null || input.isBlank()) {
            return "Command cannot be blank";
        }
        String commandName = input.trim().split(" ")[0].toLowerCase();
        if (!COMMANDS_WHICH_DO_NOT_REQUIRE_LOGIN.contains(commandName) && !userManager.isLoggedIn(socket)) {
            return "You are not logged in";
        }
        try {
            Command serverCommand = serverCommandCreator.create(input);
            return serverCommand.execute(input, socket);
        } catch (UnknownCommandException exception) {
            try {
                return inGameCommand.execute(input, socket);
            } catch (IOException ioException) {
                return "Could not process command";
            }
        } catch (IOException exception) {
            return "Could not process command";
        }
    }

}
