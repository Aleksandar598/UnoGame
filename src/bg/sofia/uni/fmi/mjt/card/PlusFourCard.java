package bg.sofia.uni.fmi.mjt.card;

import bg.sofia.uni.fmi.mjt.GameController;
import bg.sofia.uni.fmi.mjt.exception.NoColourSelectedException;

public class PlusFourCard extends ChooseColourCard {
    private static final int PENALTY_COUNT = 4;

    protected PlusFourCard(CardType type, CardColour colour, int cardID) {
        super(type, colour, cardID);
    }

    @Override
    public void applyEffect(GameController controller) throws NoColourSelectedException {
        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }
        super.applyEffect(controller);

        controller.addCardForDraw(PENALTY_COUNT);
    }

}