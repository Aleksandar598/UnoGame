package bg.sofia.uni.fmi.mjt.card;

import bg.sofia.uni.fmi.mjt.controller.GameController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PlusTwoCardTest {

    private PlusTwoCard card;
    private static final CardColour CARD_COLOUR = CardColour.RED;
    private static final CardType CARD_TYPE = CardType.SWITCH_CARD;
    private static final int CARD_ID = 1;
    private static final int CARD_COUNT_FOR_ADDING = 2;

    @BeforeEach
    public void setUp() {
        card = new PlusTwoCard(CARD_TYPE, CARD_COLOUR, CARD_ID);
    }

    @Test
    public void testApplyEffectNullCase() {
        assertThrows(IllegalArgumentException.class,
                () -> card.applyEffect(null));
    }

    @Test
    public void testApplyEffectCorrectness() {
        GameController controller = mock();
        card.applyEffect(controller);
        verify(controller).addCardForDraw(CARD_COUNT_FOR_ADDING);
    }
}