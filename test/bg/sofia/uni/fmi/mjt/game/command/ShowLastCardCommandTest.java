package bg.sofia.uni.fmi.mjt.game.command;

import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.game.card.Card;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShowLastCardCommandTest {
    private static final String LAST_CARD = "YELLOW REVERSE";
    private static final String EXPECTED_LAST_CARD = LAST_CARD + System.lineSeparator();
    private static final String EXPECTED_GAME_NOT_STARTED = "Game has not started yet";

    private GameController controller;

    @BeforeEach
    void setUp() {
        controller = Mockito.mock(GameController.class);
    }

    @Test
    void testRejectsNullController() {
        assertThrows(IllegalArgumentException.class, () -> new ShowLastCardCommand(null));
    }

    @Test
    void testReturnsTopCardWhenGameHasStarted() throws Exception {
        Card topCard = Mockito.mock(Card.class);
        when(controller.hasStarted()).thenReturn(true);
        when(controller.getTopCard()).thenReturn(topCard);
        when(topCard.getCardAsString()).thenReturn(LAST_CARD);

        String response = new ShowLastCardCommand(controller).execute();

        assertEquals(EXPECTED_LAST_CARD, response);
        verify(controller).getTopCard();
    }

    @Test
    void testRejectsShowingLastCardBeforeGameStarts() {
        when(controller.hasStarted()).thenReturn(false);

        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new ShowLastCardCommand(controller).execute());

        assertEquals(EXPECTED_GAME_NOT_STARTED, exception.getMessage());
        verify(controller, never()).getTopCard();
    }
}
