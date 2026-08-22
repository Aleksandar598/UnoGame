package bg.sofia.uni.fmi.mjt.server.command.factory;

import bg.sofia.uni.fmi.mjt.server.command.Command;
import bg.sofia.uni.fmi.mjt.server.command.CreateGameCommand;
import bg.sofia.uni.fmi.mjt.server.command.JoinGameCommand;
import bg.sofia.uni.fmi.mjt.server.command.LeaveGameCommand;
import bg.sofia.uni.fmi.mjt.server.command.ListGamesCommand;
import bg.sofia.uni.fmi.mjt.server.command.LoginCommand;
import bg.sofia.uni.fmi.mjt.server.command.LogoutCommand;
import bg.sofia.uni.fmi.mjt.server.command.RegisterCommand;
import bg.sofia.uni.fmi.mjt.server.command.StartGameCommand;
import bg.sofia.uni.fmi.mjt.server.command.SummaryCommand;
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
