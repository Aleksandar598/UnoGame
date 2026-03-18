package bg.sofia.uni.fmi.mjt.card;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StandardCardTest {

    StandardCard card;
    private StandardCard compatibleCard = new StandardCard(TYPE, CARD_COLOUR, CARD_ID + 1);
    private static final CardType TYPE = CardType.TWO;
    private static final CardColour CARD_COLOUR = CardColour.RED;
    private static final int CARD_ID = 1;

    @BeforeEach
    public void setUp() {
        card = new StandardCard(TYPE, CARD_COLOUR, CARD_ID);
    }

    @Test
    public void testConstructorNullTypeCase() {
        assertThrows(IllegalArgumentException.class,
                () -> new StandardCard(null, CARD_COLOUR, CARD_ID));
    }

    @Test
    public void testConstructorNullColourCase() {
        assertThrows(IllegalArgumentException.class,
                () -> new StandardCard(TYPE, null, CARD_ID));
    }

    @Test
    public void testIsCardPlayableNullCard() {
        assertThrows(IllegalArgumentException.class,
                () -> card.isCardPlayable(null, CARD_COLOUR));
    }

    @Test
    public void testIsCardPlayableNullCardColour() {
        assertThrows(IllegalArgumentException.class,
                () -> card.isCardPlayable(compatibleCard, null));
    }

    @Test
    public void testIsCardPlayablePositiveCase() {
        assertTrue(card.isCardPlayable(compatibleCard, CARD_COLOUR));
    }

    @Test
    public void testIsCardPlayableDifferentCardType() {
        assertTrue(card.isCardPlayable(new StandardCard(CardType.TWO, CardColour.RED, 3), CARD_COLOUR));
    }

    @Test
    public void testIsCardPlayableSameCardTypeDifferentColour() {
        assertTrue(card.isCardPlayable(new StandardCard(TYPE, CardColour.BLUE, 4), CardColour.BLUE));
    }

    @Test
    public void testIsCardPlayableNotPlayable() {
        assertFalse(card.isCardPlayable(new StandardCard(CardType.ONE, CardColour.BLUE, 5), CardColour.BLUE));
    }

    @Test
    public void testGetCardAsString() {
        String expectedString = "CardId: " + CARD_ID + ' ' + CARD_COLOUR.toString() + ' ' + TYPE.getLabel();
        assertEquals(expectedString, card.getCardAsString());
    }

}