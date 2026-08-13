package bg.sofia.uni.fmi.mjt.game.card;

import bg.sofia.uni.fmi.mjt.game.card.CardColour;
import bg.sofia.uni.fmi.mjt.game.card.CardType;
import bg.sofia.uni.fmi.mjt.game.card.ReverseDirectionCard;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ReverseDirectionCardTest {

    private ReverseDirectionCard card;
    private static final CardColour CARD_COLOUR = CardColour.RED;
    private static final CardType CARD_TYPE = CardType.SWITCH_CARD;
    private static final int CARD_ID = 1;

    @BeforeEach
    public void setUp() {
        card = new ReverseDirectionCard(CARD_TYPE, CARD_COLOUR, CARD_ID);
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
        verify(controller).reversePlayerDirection();
    }
}