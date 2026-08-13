package bg.sofia.uni.fmi.mjt.game.card;

import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.exception.NoColourSelectedException;

public abstract class AbstractColourChangingCard extends AbstractEffectCard implements ColourChangingCard, Resettable {

    private CardColour chosenColour;
    private static final CardColour DEFAULT_CARD_COLOUR = CardColour.NOT_SELECTED;

    protected AbstractColourChangingCard(CardType type, CardColour colour, int cardID) {
        super(type, colour, cardID);
        chosenColour = DEFAULT_CARD_COLOUR;
    }

    @Override
    public void setChosenColour(CardColour chosenColour) {
        if (chosenColour == null) {
            throw new IllegalArgumentException("chosenColour cannot be null");
        }

        this.chosenColour = chosenColour;
    }

    @Override
    public void reset() {
        this.chosenColour = DEFAULT_CARD_COLOUR;
    }

    @Override
    public void applyEffect(GameController controller) throws NoColourSelectedException {
        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }
        if (this.chosenColour == DEFAULT_CARD_COLOUR) {
            throw new NoColourSelectedException("chosenColour cannot be DEFAULT_CARD_COLOUR");
        }
        controller.setColour(this.chosenColour);
    }

    //package-private for testing
    CardColour getChosenColour() {
        return this.chosenColour;
    }
}
