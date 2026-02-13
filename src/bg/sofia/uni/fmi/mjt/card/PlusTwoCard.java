package bg.sofia.uni.fmi.mjt.card;

import bg.sofia.uni.fmi.mjt.GameController;

public class PlusTwoCard extends AbstractEffectCard {
    private static final int CARD_ADD_COUNT = 2;

    protected PlusTwoCard(CardType type, CardColour colour, int cardID) {
        super(type, colour, cardID);
    }

    @Override
    public void applyEffect(GameController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }
        controller.addCardForDraw(CARD_ADD_COUNT);
    }
}
