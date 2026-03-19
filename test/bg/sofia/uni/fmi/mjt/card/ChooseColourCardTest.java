package bg.sofia.uni.fmi.mjt.card;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

class ChooseColourCardTest {
    private ChooseColourCard card;
    private static final CardColour CHOSEN_COLOUR = CardColour.BLUE;
    private static final int CARD_ID = 1;

    @BeforeEach
    public void setUp() {
        this.card = new ChooseColourCard(CardType.CHOOSE_COLOUR_CARD, CardColour.SPECIAL, CARD_ID);
    }

    @Test
    public void testIsCardPlayable() {
        StandardCard topCard = mock();
        CardColour activeColour = CardColour.RED;
        assertTrue(card.isCardPlayable(topCard, activeColour));
    }

    @Test
    public void testGetCardAsString() {
        String expectedString = "CardId: " + CARD_ID + ' ' + CardType.CHOOSE_COLOUR_CARD.getLabel();
        assertEquals(expectedString, card.getCardAsString());
    }
}