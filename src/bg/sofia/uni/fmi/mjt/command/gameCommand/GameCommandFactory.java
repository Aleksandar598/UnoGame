package bg.sofia.uni.fmi.mjt.command.gameCommand;

import bg.sofia.uni.fmi.mjt.game.card.CardColour;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class GameCommandFactory {

    private GameCommandFactory() {
    }

    public static GameCommand createGameCommand(String input, GameController controller, int playerId) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Command cannot be blank");
        }
        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }
        String commandName = getCommandString(input);
        Map<String, String> arguments = parseArguments(input);

        return switch (commandName) {
            case "show-hand" -> new ShowHandCommand(controller, playerId);
            case "show-last-card" -> new ShowLastCardCommand(controller);
            case "show-played-cards" -> new ShowPlayedCardsCommand(controller);
            case "get-current-colour" -> new GetCurrentColourCommand(controller);
            case "accept-effect" -> new AcceptEffectCommand(controller, playerId);
            case "draw" -> new DrawCardCommand(playerId, controller);
            case "spectate" -> new SpectateCommand(controller, playerId);
            case "spectate-hand" -> new SpectateOtherPlayerHand(
                    controller, playerId, requiredIntArgument(arguments, "player-id"));
            case "play" -> new PlayNormalCardCommand(
                    controller, playerId, requiredIntArgument(arguments, "card-id"));
            case "play-choose", "play-plus-four" -> new PlaySpecialCardCommand(
                    controller,
                    playerId,
                    requiredIntArgument(arguments, "card-id"),
                    requiredColourArgument(arguments));
            default -> throw new IllegalArgumentException("Unknown game command: " + commandName);
        };
    }

    private static String getCommandString(String input) {
        return input.trim().split("\\s+", 2)[0].toLowerCase(Locale.ROOT);
    }

    private static Map<String, String> parseArguments(String input) {
        Map<String, String> arguments = new HashMap<>();

        String[] parts = input.split("--");
        for (int index = 1; index < parts.length; index++) {
            String part = parts[index].trim();
            int separatorIndex = part.indexOf('=');

            if (separatorIndex <= 0 || separatorIndex == part.length() - 1) {
                throw new IllegalArgumentException("Arguments must use --name=value");
            }

            String name = part.substring(0, separatorIndex).trim();
            String value = part.substring(separatorIndex + 1).trim();
            arguments.put(name, value);
        }

        return arguments;
    }

    private static int requiredIntArgument(Map<String, String> arguments, String name) {
        String value = arguments.get(name);
        if (value == null) {
            throw new IllegalArgumentException("Missing required argument: --" + name);
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Argument --" + name + " must be an integer", e);
        }
    }

    private static CardColour requiredColourArgument(Map<String, String> arguments) {
        String value = arguments.get("color");
        if (value == null) {
            throw new IllegalArgumentException("Missing required argument: --color");
        }

        try {
            return CardColour.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown colour: " + value, e);
        }
    }
}
