package bg.sofia.uni.fmi.mjt.card;

import java.util.Objects;

public abstract class AbstractStandardCard implements Card {

    private final CardType type;
    private final CardColour colour;
    private final int cardID;

    protected AbstractStandardCard(CardType type, CardColour colour, int cardID) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        if (colour == null) {
            throw new IllegalArgumentException("Colour cannot be null");
        }

        this.colour = colour;
        this.type = type;
        this.cardID = cardID;
    }

    @Override
    public CardType getCardType() {
        return this.type;
    }

    @Override
    public CardColour getCardColour() {
        return this.colour;
    }

    @Override
    public int getCardID() {
        return this.cardID;
    }

    @Override
    public boolean isCardPlayable(Card topCard, CardColour activeColour) {

        if (topCard == null) {
            throw new IllegalArgumentException("topCard cannot be null");
        }

        if (activeColour == null) {
            throw new IllegalArgumentException("cardColour cannot be null");
        }

        if (this.colour == activeColour) {
            return true;
        }

        return this.type == topCard.getCardType();
    }

    @Override
    public String getCardAsString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CardId: ")
                .append(this.cardID)
                .append(' ')
                .append(this.colour.toString())
                .append(' ')
                .append(this.type.getLabel());
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AbstractStandardCard unoCard = (AbstractStandardCard) o;
        return cardID == unoCard.cardID;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cardID);
    }
}
