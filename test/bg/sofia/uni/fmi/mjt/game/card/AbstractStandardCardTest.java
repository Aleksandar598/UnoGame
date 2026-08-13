package bg.sofia.uni.fmi.mjt.game.card;

import bg.sofia.uni.fmi.mjt.game.card.AbstractStandardCard;
import bg.sofia.uni.fmi.mjt.game.card.CardColour;
import bg.sofia.uni.fmi.mjt.game.card.CardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class AbstractStandardCardTest {
    private static class StandardCardImpl extends AbstractStandardCard {

        protected StandardCardImpl(CardType type, CardColour colour, int cardID) {
            super(type, colour, cardID);
        }
    }

    private static final int CARD_ID = 1;
    private static final CardType CARD_TYPE = CardType.SKIP_MOVE_CARD;
    private static final CardType WRONG_CARD_TYPE = CardType.SWITCH_CARD;
    private static final CardColour CARD_COLOUR = CardColour.RED;
    private static final CardColour WRONG_CARD_COLOUR = CardColour.BLUE;

    private StandardCardImpl card;

    @BeforeEach
    public void setUp() {
        card = new StandardCardImpl(CARD_TYPE, CARD_COLOUR, CARD_ID);
    }

    @Test
    public void testConstructorNullType() {
        assertThrows(IllegalArgumentException.class,
                () -> new StandardCardImpl(null, CARD_COLOUR, CARD_ID));
    }

    @Test
    public void testConstructionNullColour() {
        assertThrows(IllegalArgumentException.class,
                () -> new StandardCardImpl(CARD_TYPE, null, CARD_ID));
    }

    @Test
    public void testConstructionCorrectness() {
        assertEquals(CARD_COLOUR, card.getCardColour());
        assertEquals(CARD_TYPE, card.getCardType());
        assertEquals(CARD_ID, card.getCardID());
    }

    @Test
    public void testIsCardPlayableNullCard() {
        assertThrows(IllegalArgumentException.class,
                () -> card.isCardPlayable(null, CardColour.RED));
    }

    @Test
    public void testIsCardPlayableNullColour() {
        assertThrows(IllegalArgumentException.class,
                () -> card.isCardPlayable(new StandardCardImpl(CARD_TYPE, CARD_COLOUR, CARD_ID + 1), null));
    }

    @Test
    public void testIsCardPlayableWrongColourWrongType() {
        assertFalse(card.isCardPlayable(new StandardCardImpl(WRONG_CARD_TYPE, WRONG_CARD_COLOUR, CARD_ID + 1), WRONG_CARD_COLOUR));
    }

    @Test
    public void testIsCardPlayableRightColourWrongType() {
        assertTrue(card.isCardPlayable(new StandardCardImpl(WRONG_CARD_TYPE, WRONG_CARD_COLOUR, CARD_ID + 1), CARD_COLOUR));
    }

    @Test
    public void testIsCardPlayableWrongColourRightType() {
        assertTrue(card.isCardPlayable(new StandardCardImpl(CARD_TYPE, WRONG_CARD_COLOUR, CARD_ID + 1), WRONG_CARD_COLOUR));
    }

    @Test
    public void testIsCardPlayableRightColourRightType() {
        assertTrue(card.isCardPlayable(new StandardCardImpl(CARD_TYPE, CARD_COLOUR, CARD_ID + 1), CARD_COLOUR));

    }

    @Test
    public void testGetCardAsStringCorrectness() {
        String expectedString = "CardId: " + CARD_ID + ' ' + CARD_COLOUR.toString() + ' ' + CARD_TYPE.getLabel();
        assertEquals(expectedString, card.getCardAsString());
    }




}