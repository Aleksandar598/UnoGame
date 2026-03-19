package bg.sofia.uni.fmi.mjt.card;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CardTest {

    private static final int CARD_ID = 1;
    private static final CardColour CARD_COLOUR = CardColour.RED;

    @Test
    public void testOfNullType() {
        assertThrows(IllegalArgumentException.class,
                () -> Card.of(CARD_COLOUR, null, CARD_ID));
    }

    @Test
    public void testOfNullColour() {
        assertThrows(IllegalArgumentException.class,
                () -> Card.of(null, CardType.CHOOSE_COLOUR_CARD, CARD_ID));
    }

    //this test checks all attributes manually as equals function checks only for ID
    @Test
    public void testOfStandardCard() {
        Card expected = new StandardCard(CardType.ONE, CARD_COLOUR, CARD_ID);
        generateAndCompareTheCards(expected, CardType.ONE, CARD_COLOUR, CARD_ID);
    }

    @Test
    public void testOfSkipTurnCard() {
        Card expected = new SkipTurnCard(CardType.SKIP_MOVE_CARD, CARD_COLOUR, CARD_ID);
        generateAndCompareTheCards(expected, CardType.SKIP_MOVE_CARD, CARD_COLOUR, CARD_ID);
    }

    @Test
    public void testOfReverseDirectionCard() {
        Card expected = new ReverseDirectionCard(CardType.SWITCH_CARD, CARD_COLOUR, CARD_ID);
        generateAndCompareTheCards(expected, CardType.SWITCH_CARD, CARD_COLOUR, CARD_ID);
    }

    @Test
    public void testOfPlusTwoCard() {
        Card expected = new PlusTwoCard(CardType.PLUS_TWO_CARD, CARD_COLOUR, CARD_ID);
        generateAndCompareTheCards(expected, CardType.PLUS_TWO_CARD, CARD_COLOUR, CARD_ID);
    }

    @Test
    public void testOfPlusFourCard() {
        Card expected = new PlusFourCard(CardType.PLUS_FOUR_CARD, CardColour.SPECIAL, CARD_ID);
        generateAndCompareTheCards(expected, CardType.PLUS_FOUR_CARD, CardColour.SPECIAL, CARD_ID);
    }

    @Test
    public void testOfChangeColourCard() {
        Card expected = new ChooseColourCard(CardType.CHOOSE_COLOUR_CARD, CardColour.SPECIAL, CARD_ID);
        generateAndCompareTheCards(expected, CardType.CHOOSE_COLOUR_CARD, CardColour.SPECIAL, CARD_ID);
    }
    private void generateAndCompareTheCards(Card expected, CardType type, CardColour colour, int cardId) {
        Card actual = Card.of(colour, type, cardId);
        assertEquals(expected, actual);
        assertEquals(expected.getCardColour(), actual.getCardColour());
        assertEquals(expected.getCardID(), actual.getCardID());
        assertEquals(expected.getCardType(), actual.getCardType());
    }
}