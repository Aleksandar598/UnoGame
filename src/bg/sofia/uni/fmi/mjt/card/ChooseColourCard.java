package bg.sofia.uni.fmi.mjt.card;

import bg.sofia.uni.fmi.mjt.controller.GameController;
import bg.sofia.uni.fmi.mjt.exception.NoColourSelectedException;

public class ChooseColourCard extends AbstractEffectCard {

    ChooseColourCard(CardType type, CardColour colour, int cardID) {
        super(type, colour, cardID);
    }

    @Override
    public void applyEffect(GameController controller) {

    }

    @Override
    public boolean isCardPlayable(Card topCard, CardColour activeColour) {
        return true;
    }

    @Override
    public String getCardAsString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CardId: ")
                .append(getCardID())
                .append(' ')
                .append(getCardType().getLabel());
        return builder.toString();
    }

}
