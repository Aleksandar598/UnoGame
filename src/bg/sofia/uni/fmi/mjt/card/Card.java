package bg.sofia.uni.fmi.mjt.card;

public interface Card {

    int getCardID();

    CardType getCardType();

    CardColour getCardColour();

    /**
     *
     * @param topCard the card that is on top in the pile
     * @return true if this card can be played, false if it cannot be played
     */
    boolean isCardPlayable(Card topCard, CardColour currentColour);

    String getCardAsString();

    /**
     *
     * @param colour The colour of the requested card, if there is no colour give CardColour.SPECIAL.
     * @param type the type of the card requested
     * @param id the id of the requested card
     * @return a new card with the given parameters
     */
    static Card of(CardColour colour, CardType type, int id) {

        if (colour == null) {
            throw new IllegalArgumentException("Colour cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }

        Card card;
        switch (type) {
            case CardType.CHOOSE_COLOUR_CARD -> card = new ChooseColourCard(type, CardColour.SPECIAL, id);
            case CardType.PLUS_FOUR_CARD -> card = new PlusFourCard(type, CardColour.SPECIAL, id);
            case CardType.PLUS_TWO_CARD -> card = new PlusTwoCard(type, colour, id);
            case CardType.SKIP_MOVE_CARD -> card = new SkipTurnCard(type, colour, id);
            case CardType.SWITCH_CARD -> card = new ReverseDirectionCard(type, colour, id);
            default -> card = new StandardCard(type, colour, id);
        }
        return card;
    }
}

