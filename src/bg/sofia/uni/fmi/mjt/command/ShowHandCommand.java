package bg.sofia.uni.fmi.mjt.command;

import bg.sofia.uni.fmi.mjt.controller.GameController;
import bg.sofia.uni.fmi.mjt.card.Card;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.player.Player;

import java.util.List;

public class ShowHandCommand implements Command {

    private final GameController controller;
    private final int playerId;
    public ShowHandCommand(GameController controller, int playerId) throws UnoUserException {
        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }
        this.controller = controller;
        this.playerId = playerId;
    }

    @Override
    public String execute() throws UnoUserException {
        Player player;

        if (!controller.hasStarted()) {
            throw new UnoUserException("Game has not started yet");
        }

        try {
            player = controller.getPlayer(playerId);

        } catch (PlayerNotFoundException e) {
            throw new UnoUserException("You are not in this game!", e);
        }

        StringBuilder stringBuilder = new StringBuilder();
        List<Card> hand = player.getCards();
        int iter = 0;

        for (Card c : hand) {
            stringBuilder.append(iter)
                    .append(". ")
                    .append(c.getCardAsString())
                    .append("\n");
            iter++;
        }
        return stringBuilder.toString();
    }
}
