package bg.sofia.uni.fmi.mjt.command.serverCommand.factory;

import bg.sofia.uni.fmi.mjt.command.serverCommand.Command;
import bg.sofia.uni.fmi.mjt.command.serverCommand.CreateGameCommand;
import bg.sofia.uni.fmi.mjt.command.serverCommand.JoinGameCommand;
import bg.sofia.uni.fmi.mjt.command.serverCommand.LeaveGameCommand;
import bg.sofia.uni.fmi.mjt.command.serverCommand.ListGamesCommand;
import bg.sofia.uni.fmi.mjt.command.serverCommand.LoginCommand;
import bg.sofia.uni.fmi.mjt.command.serverCommand.LogoutCommand;
import bg.sofia.uni.fmi.mjt.command.serverCommand.RegisterCommand;
import bg.sofia.uni.fmi.mjt.command.serverCommand.StartGameCommand;
import bg.sofia.uni.fmi.mjt.command.serverCommand.SummaryCommand;
import bg.sofia.uni.fmi.mjt.server.exception.UnknownCommandException;

public class CommandFactory {

    private CommandFactory() {

    }

    public static Command getCommand(String input) throws UnknownCommandException {
        if (input == null ) {
            throw new IllegalArgumentException("Input is null");
        }

        String commandName = getCommandString(input);
        return switch (commandName) {
            case "register" -> new RegisterCommand();
            case "login" -> new LoginCommand();
            case "logout" -> new LogoutCommand();
            case "list-games" -> new ListGamesCommand();
            case "create-game" -> new CreateGameCommand();
            case "join" -> new JoinGameCommand();
            case "start" -> new StartGameCommand();
            case "leave" -> new LeaveGameCommand();
            case "summary" -> new SummaryCommand();
            default -> throw new UnknownCommandException("Unknown server command: " + commandName);
        };
    }

    private static String getCommandString(String input) {
        return input.split(" ")[0];
    }
}
