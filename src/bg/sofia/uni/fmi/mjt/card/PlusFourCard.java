package bg.sofia.uni.fmi.mjt.card;

import bg.sofia.uni.fmi.mjt.controller.GameController;
import bg.sofia.uni.fmi.mjt.exception.NoColourSelectedException;

public class PlusFourCard extends ChooseColourCard {
    private static final int PENALTY_COUNT = 4;

    protected PlusFourCard(CardType type, CardColour colour, int cardID) {
        super(type, colour, cardID);
    }

    @Override
    public void applyEffect(GameController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }
        controller.addCardForDraw(PENALTY_COUNT);
    }

}