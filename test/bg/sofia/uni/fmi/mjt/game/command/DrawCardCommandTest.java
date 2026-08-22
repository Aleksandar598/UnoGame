package bg.sofia.uni.fmi.mjt.game.command;

import bg.sofia.uni.fmi.mjt.exception.CannotPlayCardException;
import bg.sofia.uni.fmi.mjt.exception.GameNotStartedException;
import bg.sofia.uni.fmi.mjt.exception.InvalidActionException;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.game.card.Card;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DrawCardCommandTest {
    private static final int PLAYER_ID = 7;
    private static final String DRAWN_CARD = "BLUE FIVE";
    private static final String EXPECTED_SUCCESSFUL_DRAW = "Card drawn: " + DRAWN_CARD;
    private static final String EXPECTED_PLAYER_NOT_FOUND = "Player not found";
    private static final String EXPECTED_CANNOT_DRAW = "You cannot draw a card now";
    private static final String EXPECTED_GAME_NOT_STARTED = "Game has not started";
    private static final String EXPECTED_INVALID_ACTION = "Invalid action";

    private GameController controller;

    @BeforeEach
    void setUp() {
        controller = Mockito.mock(GameController.class);
    }

    @Test
    void testRejectsNullController() {
        assertThrows(IllegalArgumentException.class, () -> new DrawCardCommand(PLAYER_ID, null));
    }

    @Test
    void testReturnsDrawnCardMessage() throws Exception {
        Card card = Mockito.mock(Card.class);
        when(card.getCardAsString()).thenReturn(DRAWN_CARD);
        when(controller.drawCard(PLAYER_ID)).thenReturn(card);

        String response = new DrawCardCommand(PLAYER_ID, controller).execute();

        assertEquals(EXPECTED_SUCCESSFUL_DRAW, response);
        verify(controller).drawCard(PLAYER_ID);
    }

    @Test
    void testWrapsPlayerNotFoundExceptionAsUnoUserException() throws Exception {
        when(controller.drawCard(PLAYER_ID)).thenThrow(new PlayerNotFoundException(EXPECTED_PLAYER_NOT_FOUND));

        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new DrawCardCommand(PLAYER_ID, controller).execute());

        assertEquals(EXPECTED_PLAYER_NOT_FOUND, exception.getMessage());
    }

    @Test
    void testWrapsCannotPlayCardExceptionAsUnoUserException() throws Exception {
        when(controller.drawCard(PLAYER_ID)).thenThrow(new CannotPlayCardException(EXPECTED_CANNOT_DRAW));

        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new DrawCardCommand(PLAYER_ID, controller).execute());

        assertEquals(EXPECTED_CANNOT_DRAW, exception.getMessage());
    }

    @Test
    void testWrapsGameNotStartedExceptionAsUnoUserException() throws Exception {
        when(controller.drawCard(PLAYER_ID)).thenThrow(new GameNotStartedException(EXPECTED_GAME_NOT_STARTED));

        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new DrawCardCommand(PLAYER_ID, controller).execute());

        assertEquals(EXPECTED_GAME_NOT_STARTED, exception.getMessage());
    }

    @Test
    void testWrapsInvalidActionExceptionAsUnoUserException() throws Exception {
        when(controller.drawCard(PLAYER_ID)).thenThrow(new InvalidActionException(EXPECTED_INVALID_ACTION));

        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new DrawCardCommand(PLAYER_ID, controller).execute());

        assertEquals(EXPECTED_INVALID_ACTION, exception.getMessage());
    }
}
