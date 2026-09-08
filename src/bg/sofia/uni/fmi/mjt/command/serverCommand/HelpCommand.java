package bg.sofia.uni.fmi.mjt.command.serverCommand;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/** Lists the commands understood by the UNO server. */
public class HelpCommand extends AbstractCommand {
    private static final String HELP = String.join(System.lineSeparator(),
            "Available commands:",
            "register --username=<username> --password=<password>",
            "login --username=<username> --password=<password>",
            "logout",
            "list-games [--status=all|available|started|ended]",
            "create-game --game-id=<id> [--number-of-players=<count>]",
            "join --game-id=<id> [--display-name=<name>]",
            "start",
            "leave",
            "summary --game-id=<id>",
            "Game commands: show-hand, show-last-card, show-played-cards, get-current-colour,",
            "accept-effect, draw, spectate, spectate-hand --player-id=<id>, play --card-id=<id>,",
            "play-choose --card-id=<id> --color=<color>, play-plus-four --card-id=<id> --color=<color>");

    @Override
    public String execute(String input, SocketChannel socket) throws IOException {
        return HELP;
    }
}
