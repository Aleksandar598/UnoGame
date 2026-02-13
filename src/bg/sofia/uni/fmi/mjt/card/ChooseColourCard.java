package bg.sofia.uni.fmi.mjt.card;

import bg.sofia.uni.fmi.mjt.GameController;
import bg.sofia.uni.fmi.mjt.exception.NoColourSelectedException;

public class ChooseColourCard extends AbstractEffectCard {
    CardColour chosenColour = CardColour.SPECIAL;

    ChooseColourCard(CardType type, CardColour colour, int cardID) {
        super(type, colour, cardID);
    }

    public void setChosenColour(CardColour chosenColour) {
        if (chosenColour == null) {
            throw new IllegalArgumentException("chosenColour cannot be null");
        }
        this.chosenColour = chosenColour;
    }

    public void reset() {
        this.chosenColour = CardColour.SPECIAL;
    }

    @Override
    public void applyEffect(GameController controller) throws NoColourSelectedException {
        if (chosenColour == CardColour.SPECIAL) {
            throw new NoColourSelectedException("There is no selected colour for the chooseColour card");
        }
        controller.setColour(chosenColour);
    }

    @Override
    public boolean isCardPlayable(Card topCard, CardColour activeColour) {
        return true;
    }
}
