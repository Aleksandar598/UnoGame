package bg.sofia.uni.fmi.mjt.game.card;

import bg.sofia.uni.fmi.mjt.game.card.CardColour;
import bg.sofia.uni.fmi.mjt.game.card.CardType;
import bg.sofia.uni.fmi.mjt.game.card.PlusFourCard;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.exception.NoColourSelectedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


class PlusFourCardTest {

    private PlusFourCard card;
    private static final CardColour CHOSEN_COLOUR = CardColour.BLUE;
    private static final CardColour CARD_COLOUR = CardColour.NOT_SELECTED;
    private static final CardType CARD_TYPE = CardType.PLUS_FOUR_CARD;
    private static final int CARD_ID = 1;

    @BeforeEach
    public void setUp() {
        card = new PlusFourCard(CardType.PLUS_FOUR_CARD, CardColour.SPECIAL, CARD_ID);
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
    public void testIsCardPlayableCorrectness() { //this should always return true anyway
        assertTrue(card.isCardPlayable(null, null));
    }

    @Test
    public void testGetCardAsStringCorrectness() {
        String expectedString = "CardId: " + CARD_ID + ' ' + CARD_TYPE.getLabel();
        assertEquals(expectedString, card.getCardAsString());
    }
}