package bg.sofia.uni.fmi.mjt.game.card;

import bg.sofia.uni.fmi.mjt.game.card.CardColour;
import bg.sofia.uni.fmi.mjt.game.card.CardType;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.exception.NoColourSelectedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AbstractColourChangingCardTest {
    private static class TestChangingColourCard extends AbstractColourChangingCard{

        protected TestChangingColourCard(CardType type, CardColour colour, int cardID) {
            super(type, colour, cardID);
        }
    }

    private TestChangingColourCard card;
    private static final CardColour CARD_COLOUR = CardColour.SPECIAL;
    private static final CardColour CHOSEN_COLOUR = CardColour.BLUE;
    private static final CardColour DEFAULT_CHOSEN_COLOUR = CardColour.NOT_SELECTED;
    private static final CardType CARD_TYPE = CardType.CHOOSE_COLOUR_CARD;
    private static final int CARD_ID = 1;

    @BeforeEach
    public void setUp() {
        card = new TestChangingColourCard(CARD_TYPE, CARD_COLOUR, CARD_ID);
    }

    @Test
    public void testDefaultColourCorrectness() {
        assertEquals(DEFAULT_CHOSEN_COLOUR, card.getChosenColour());
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
    public void testResetCorrectness() {
        card.setChosenColour(CHOSEN_COLOUR);
        card.reset();
        assertEquals(DEFAULT_CHOSEN_COLOUR, card.getChosenColour());
    }

    @Test
    public void testApplyEffectNullCase() {
        card.setChosenColour(CHOSEN_COLOUR);
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
    public void testApplyEffectCorrectness() throws NoColourSelectedException {
        GameController controller = mock();
        card.setChosenColour(CHOSEN_COLOUR);
        card.applyEffect(controller);
        verify(controller).setColour(CHOSEN_COLOUR);
    }
}