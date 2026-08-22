package bg.sofia.uni.fmi.mjt.server.command.broadcast;

import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.server.game.GameManager;

public class GameEventBroadcaster {

    private static final String DRAW_MESSAGE = "drew a card";

    public static void broadcast(String message, GameManager manager, String command, Player player) {
        String name = command.split(" ")[0];
        switch (name) {
            case "play", "play-choose", "play-plus-four" -> {
                manager.notifyAllInAGame(player, player.getName() + " played " + message);
            }
            case "draw" -> {
                manager.notifyAllInAGame(player, player.getName() + " " + DRAW_MESSAGE);
            }
            case "leave" -> {
                manager.notifyAllInAGame(player, player.getName() + " left");
            }
            default -> {
                return;
            }
        }
    }

}
