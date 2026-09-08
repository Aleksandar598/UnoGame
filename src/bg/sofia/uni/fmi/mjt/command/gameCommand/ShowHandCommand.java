package bg.sofia.uni.fmi.mjt.command.gameCommand;

import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.game.card.Card;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.server.exception.ExceptionLogger;

import java.io.IOException;
import java.util.List;

public class ShowHandCommand implements GameCommand {

    private final GameController controller;
    private final int playerId;

    public ShowHandCommand(GameController controller, int playerId) {
        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }
        this.controller = controller;
        this.playerId = playerId;
    }

    @Override
    public String execute() throws UnoUserException, IOException {
        if (!controller.hasStarted()) {
            throw new UnoUserException("Game has not started yet");
        }

        Player player;
        try {
            player = controller.getPlayer(playerId);

        } catch (PlayerNotFoundException e) {
            ExceptionLogger.logGameException(e);
            throw new UnoUserException("Player not in game", e);
        }

        StringBuilder stringBuilder = new StringBuilder();
        List<Card> hand = player.getCards();
        int iter = 0;

        for (Card c : hand) {
            stringBuilder.append(iter)
                    .append(". ")
                    .append(c.getCardAsString())
                    .append(System.lineSeparator());
            iter++;
        }
        return stringBuilder.toString();
    }
}
