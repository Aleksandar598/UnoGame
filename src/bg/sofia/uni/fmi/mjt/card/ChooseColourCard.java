package bg.sofia.uni.fmi.mjt.card;

public class ChooseColourCard extends AbstractEffectCard {
    private static final CardColour DEFAULT_CARD_COLOUR = CardColour.SPECIAL;

    ChooseColourCard(CardType type, CardColour colour, int cardID) {
        super(type, colour, cardID);
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
