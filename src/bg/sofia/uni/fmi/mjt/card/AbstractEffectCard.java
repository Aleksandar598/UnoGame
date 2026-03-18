package bg.sofia.uni.fmi.mjt.card;

import bg.sofia.uni.fmi.mjt.controller.GameController;
import bg.sofia.uni.fmi.mjt.exception.NoColourSelectedException;

public abstract class AbstractEffectCard extends AbstractStandardCard implements EffectCard {

    private CardColour chosenColour = CardColour.NOT_SELECTED;

    protected AbstractEffectCard(CardType type, CardColour colour, int cardID) {
        super(type, colour, cardID);
    }

    @Override
    public void reset() {
        this.chosenColour = CardColour.NOT_SELECTED;
    }

    @Override
    public void setChosenColour(CardColour colour) {
        if (colour == null) {
            throw new IllegalArgumentException("CardColour cannot be null");
        }
        this.chosenColour = colour;
    }

    @Override
    public void applyEffect(GameController controller) throws NoColourSelectedException {

        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }
        if (chosenColour == CardColour.NOT_SELECTED) {
            throw new NoColourSelectedException("Colour is not selected");
        }

        controller.setColour(this.chosenColour);
    }

    //package-private so we can test if it resets the colour
    CardColour getChosenColour() {
        return this.chosenColour;
    }
}
