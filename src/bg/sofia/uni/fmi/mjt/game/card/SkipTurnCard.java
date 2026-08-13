package bg.sofia.uni.fmi.mjt.game.card;

import bg.sofia.uni.fmi.mjt.game.controller.GameController;

public class SkipTurnCard extends AbstractEffectCard {

    protected SkipTurnCard(CardType type, CardColour colour, int cardID) {
        super(type, colour, cardID);
    }

    @Override
    public void applyEffect(GameController controller) {

        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }
        controller.skipNextPlayer();
    }
}
