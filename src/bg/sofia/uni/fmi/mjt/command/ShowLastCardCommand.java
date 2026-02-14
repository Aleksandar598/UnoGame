package bg.sofia.uni.fmi.mjt.command;

import bg.sofia.uni.fmi.mjt.GameController;
import bg.sofia.uni.fmi.mjt.card.Card;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;

public class ShowLastCardCommand implements Command {

    private final GameController controller;

    public ShowLastCardCommand(GameController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }
        this.controller = controller;
    }

    @Override
    public String execute() throws UnoUserException {
        StringBuilder stringBuilder = new StringBuilder();

        Card topCard = controller.getTopCard();
        stringBuilder.append(topCard.getCardAsString())
                .append('\n');

        return stringBuilder.toString();
    }
}
