package bg.sofia.uni.fmi.mjt.card;

import bg.sofia.uni.fmi.mjt.controller.GameController;
import bg.sofia.uni.fmi.mjt.exception.NoColourSelectedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class PlusFourCardTest {

    private PlusFourCard card;
    private static final CardColour CHOSEN_COLOUR = CardColour.BLUE;
    private static final CardColour CARD_COLOUR = CardColour.SPECIAL;
    private static final CardType CARD_TYPE = CardType.PLUS_FOUR_CARD;
    private static final int CARD_ID = 1;

    @BeforeEach
    public void setUp() {
        card = new PlusFourCard(CardType.PLUS_FOUR_CARD, CardColour.SPECIAL, CARD_ID);
    }

    @Test
    public void testConstructorNullType() {
        assertThrows(IllegalArgumentException.class,
                () -> new PlusFourCard(null, CARD_COLOUR, CARD_ID));
    }

    @Test
    public void testConstructorNullColour() {
        assertThrows(IllegalArgumentException.class,
                () -> new PlusFourCard(CARD_TYPE, null, CARD_ID));
    }

    @Test
    public void testResetMethodCorrectness() {
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
        assertEquals(CHOSEN_COLOUR, card.getChosenColour());
    }

    @Test
    public void testApplyEffectNullController() throws NoColourSelectedException {
        assertThrows(IllegalArgumentException.class,
                () -> card.applyEffect(null));
    }

    @Test
    public void testApplyEffectNoSelectedColour() {
        GameController controller = mock();
        assertThrows(NoColourSelectedException.class,
                () -> card.applyEffect(controller));
    }

    @Test
    public void testApplyEffectNormalCase() throws NoColourSelectedException {
        GameController controller = mock();
        card.setChosenColour(CHOSEN_COLOUR);
        card.applyEffect(controller);
        verify(controller).setColour(CHOSEN_COLOUR);
        verify(controller).addCardForDraw(4); //4 cards will be added
    }

    @Test
    public void testIsCardPlayableCorrectness() { //this should always return true anyways
        assertTrue(card.isCardPlayable(null, null));
    }

    @Test
    public void testGetCardAsStringCorrectness() {
        String expectedString = "CardId: " + CARD_ID + ' ' + CARD_TYPE.getLabel();
        assertEquals(expectedString, card.getCardAsString());
    }
}