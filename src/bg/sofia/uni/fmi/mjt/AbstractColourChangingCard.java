package bg.sofia.uni.fmi.mjt;

import bg.sofia.uni.fmi.mjt.card.CardColour;
import bg.sofia.uni.fmi.mjt.card.ColourChangingCard;
import bg.sofia.uni.fmi.mjt.card.Resettable;

public class AbstractColourChangingCard implements ColourChangingCard, Resettable {

    private CardColour chosenColour = CardColour.NOT_SELECTED;

    @Override
    public void setChosenColour(CardColour chosenColour) {
        if (chosenColour == null) {
            throw new IllegalArgumentException("chosenColour cannpt be null");
        }

        this.chosenColour = chosenColour;
    }

    @Override
    public void reset() {
        this.chosenColour = CardColour.NOT_SELECTED;
    }
}
