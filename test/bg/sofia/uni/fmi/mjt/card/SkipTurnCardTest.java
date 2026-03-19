package bg.sofia.uni.fmi.mjt.card;


import bg.sofia.uni.fmi.mjt.controller.GameController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SkipTurnCardTest {
    private SkipTurnCard card;
    private static final int CARD_ID = 1;
    private static final CardColour CARD_COLOUR = CardColour.RED;
    private static final CardType CARD_TYPE = CardType.SKIP_MOVE_CARD;

    @BeforeEach
    public void setUp() {
        card = new SkipTurnCard(CARD_TYPE, CARD_COLOUR, CARD_ID);
    }

    @Test
    public void testApplyEffectNullController() {
        assertThrows(IllegalArgumentException.class,
                () -> card.applyEffect(null));
    }

    @Test
    public void testApplyEffectCorrectness() {
        GameController controller = mock();
        card.applyEffect(controller);
        verify(controller).skipNextPlayer();
    }
}