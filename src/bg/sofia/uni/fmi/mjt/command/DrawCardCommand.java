package bg.sofia.uni.fmi.mjt.command;

import bg.sofia.uni.fmi.mjt.controller.GameController;
import bg.sofia.uni.fmi.mjt.card.Card;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;

public class DrawCardCommand implements Command {
    private final int playerId;
    private final GameController controller;

    public DrawCardCommand(int playerId, GameController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }
        this.playerId = playerId;
        this.controller = controller;
    }

    @Override
    public String execute() throws UnoUserException {

        try {
            if (controller.checkPlayerCanPlayAnyCards(playerId)) {
                throw new UnoUserException("You can play a card");
            }
            if (!controller.hasStarted()) {
                throw new UnoUserException("Game has not started yet");
            }
            Card c = controller.drawCard(playerId);
            StringBuilder drawnCard = new StringBuilder();
            drawnCard.append("Card drawn: ")
                    .append(c.getCardAsString());
            controller.nextTurn();
            return drawnCard.toString();
        } catch (PlayerNotFoundException e) {
            throw new UnoUserException("Player not found", e);
        }
    }
}
