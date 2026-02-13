package bg.sofia.uni.fmi.mjt.card;

import bg.sofia.uni.fmi.mjt.GameController;

public class ReverseDirectionCard extends AbstractEffectCard {

    protected ReverseDirectionCard(CardType type, CardColour colour, int cardID) {
        super(type, colour, cardID);
    }

    @Override
    public void applyEffect(GameController controller) {

        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }
        controller.reversePlayerDirection();
    }
}
