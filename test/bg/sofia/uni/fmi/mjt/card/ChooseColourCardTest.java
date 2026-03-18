package bg.sofia.uni.fmi.mjt.card;

import bg.sofia.uni.fmi.mjt.controller.GameController;
import bg.sofia.uni.fmi.mjt.exception.NoColourSelectedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ChooseColourCardTest {
    private ChooseColourCard card;
    private static final CardColour CHOSEN_COLOUR = CardColour.BLUE;
    private static final int CARD_ID = 1;

    @BeforeEach
    public void makeCard() {
        this.card = new ChooseColourCard(CardType.CHOOSE_COLOUR_CARD, CardColour.SPECIAL, CARD_ID);
    }

    @Test
    public void testConstructorNullCardType() {
        assertThrows(IllegalArgumentException.class,
                () -> new ChooseColourCard(null, CardColour.SPECIAL, CARD_ID));
    }

    @Test
    public void testConstructorNUllCardColour() {
        assertThrows(IllegalArgumentException.class,
                () -> new ChooseColourCard(CardType.CHOOSE_COLOUR_CARD, null, CARD_ID));
    }

    @Test
    public void testResetFunctionCorrectness() {
        card.setChosenColour(CHOSEN_COLOUR);
        card.reset();
        assertEquals(CardColour.NOT_SELECTED, card.getChosenColour());
    }

    @Test
    public void testSetChosenColourNullCase() {
        assertThrows(IllegalArgumentException.class,
                () -> card.setChosenColour(null));
    }

    @Test
    public void testSetChosenColourCorrectness() {
        card.setChosenColour(CHOSEN_COLOUR);
        assertEquals(CardColour.BLUE, card.getChosenColour());
    }

    @Test
    public void testApplyEffectNullCase() {
        assertThrows(IllegalArgumentException.class,
                () -> card.applyEffect(null));
    }

    @Test
    public void testApplyEffectNoColourSelected() {
        GameController controller = mock();
        assertThrows(NoColourSelectedException.class,
                () -> card.applyEffect(controller));
    }

    @Test
    public void testGetColour() {
        assertEquals(CardColour.SPECIAL, card.getCardColour());
    }

    @Test
    public void testApplyEffectCorrectness() throws NoColourSelectedException {
        GameController controller = mock();
        card.setChosenColour(CHOSEN_COLOUR);
        card.applyEffect(controller);
        verify(controller).setColour(CHOSEN_COLOUR);
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