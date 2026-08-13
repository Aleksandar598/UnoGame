package bg.sofia.uni.fmi.mjt.game.card;

import bg.sofia.uni.fmi.mjt.game.card.CardColour;
import bg.sofia.uni.fmi.mjt.game.card.CardType;
import bg.sofia.uni.fmi.mjt.game.card.ChooseColourCard;
import bg.sofia.uni.fmi.mjt.game.card.StandardCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;


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