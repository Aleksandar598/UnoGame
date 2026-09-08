package bg.sofia.uni.fmi.mjt.game.command;

import bg.sofia.uni.fmi.mjt.command.gameCommand.AcceptEffectCommand;
import bg.sofia.uni.fmi.mjt.exception.CannotPlayCardException;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AcceptEffectCommandTest {
    private static final int PLAYER_ID = 7;
    private static final int PENALTY_CARD_COUNT = 4;
    private static final String ACCEPTED_EFFECT_DETAILS = "0. BLUE ONE" + System.lineSeparator() +"1. BLUE TWO" + System.lineSeparator();
    private static final String EXPECTED_SUCCESSFUL_ACCEPT_EFFECT = "Drawn 4 cards:"
            + System.lineSeparator() + ACCEPTED_EFFECT_DETAILS;
    private static final String EXPECTED_PLAYER_NOT_FOUND = "Player not found";
    private static final String EXPECTED_NO_PENDING_EFFECT = "There is no pending effect";

    private GameController controller;

    @BeforeEach
    void setUp() {
        controller = Mockito.mock(GameController.class);
    }

    @Test
    void testRejectsNullController() {
        assertThrows(IllegalArgumentException.class, () -> new AcceptEffectCommand(null, PLAYER_ID));
    }

    @Test
    void testReturnsDrawnCardsMessageAfterAcceptingEffect() throws Exception {
        when(controller.penaltyCardCount()).thenReturn(PENALTY_CARD_COUNT);
        when(controller.acceptPenalty(PLAYER_ID)).thenReturn(ACCEPTED_EFFECT_DETAILS);

        String response = new AcceptEffectCommand(controller, PLAYER_ID).execute();

        assertEquals(EXPECTED_SUCCESSFUL_ACCEPT_EFFECT, response);
        verify(controller).acceptPenalty(PLAYER_ID);
    }

    @Test
    void testWrapsPlayerNotFoundExceptionAsUnoUserException() throws Exception {
        when(controller.acceptPenalty(PLAYER_ID))
                .thenThrow(new PlayerNotFoundException(EXPECTED_PLAYER_NOT_FOUND));

        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new AcceptEffectCommand(controller, PLAYER_ID).execute());

        assertEquals(EXPECTED_PLAYER_NOT_FOUND, exception.getMessage());
    }

    @Test
    void testWrapsCannotPlayCardExceptionAsUnoUserException() throws Exception {
        when(controller.acceptPenalty(PLAYER_ID))
                .thenThrow(new CannotPlayCardException(EXPECTED_NO_PENDING_EFFECT));

        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new AcceptEffectCommand(controller, PLAYER_ID).execute());

        assertEquals(EXPECTED_NO_PENDING_EFFECT, exception.getMessage());
    }
}
