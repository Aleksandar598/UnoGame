package bg.sofia.uni.fmi.mjt.game.command;

import bg.sofia.uni.fmi.mjt.command.gameCommand.PlaySpecialCardCommand;
import bg.sofia.uni.fmi.mjt.exception.CannotPlayCardException;
import bg.sofia.uni.fmi.mjt.exception.CardNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.GameNotStartedException;
import bg.sofia.uni.fmi.mjt.exception.NoColourSelectedException;
import bg.sofia.uni.fmi.mjt.exception.NotAColourChangeCardException;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.game.card.CardColour;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlaySpecialCardCommandTest {
    private static final int PLAYER_ID = 7;
    private static final int CARD_ID = 12;
    private static final CardColour SELECTED_COLOUR = CardColour.BLUE;
    private static final String EXPECTED_SUCCESSFUL_PLAY = "Success";
    private static final String EXPECTED_CARD_NOT_FOUND = "Card not found";
    private static final String EXPECTED_NO_COLOUR_SELECTED = "No colour selected";
    private static final String EXPECTED_NOT_A_COLOUR_CHANGE_CARD = "Card cannot change colour";
    private static final String EXPECTED_PLAYER_NOT_FOUND = "Player not found";
    private static final String EXPECTED_CANNOT_PLAY_CARD = "Cannot play card";
    private static final String EXPECTED_GAME_NOT_STARTED = "Game has not started";

    private GameController controller;

    @BeforeEach
    void setUp() {
        controller = Mockito.mock(GameController.class);
    }

    @Test
    void testRejectsNullController() {
        assertThrows(IllegalArgumentException.class,
                () -> new PlaySpecialCardCommand(null, PLAYER_ID, CARD_ID, SELECTED_COLOUR));
    }

    @Test
    void testRejectsNullColour() {
        assertThrows(IllegalArgumentException.class,
                () -> new PlaySpecialCardCommand(controller, PLAYER_ID, CARD_ID, null));
    }

    @Test
    void testPlaysSpecialCardAdvancesTurnAndReturnsSuccess() throws Exception {
        when(controller.playCard(PLAYER_ID, CARD_ID, SELECTED_COLOUR)).thenReturn(EXPECTED_SUCCESSFUL_PLAY);
        String response = new PlaySpecialCardCommand(controller, PLAYER_ID, CARD_ID, SELECTED_COLOUR).execute();

        assertEquals(EXPECTED_SUCCESSFUL_PLAY, response);
        verify(controller).playCard(PLAYER_ID, CARD_ID, SELECTED_COLOUR);
        verify(controller).nextTurn();
    }

    @Test
    void testWrapsCardNotFoundExceptionAsUnoUserException() throws Exception {
        doThrow(new CardNotFoundException(EXPECTED_CARD_NOT_FOUND))
                .when(controller).playCard(PLAYER_ID, CARD_ID, SELECTED_COLOUR);

        assertCommandException(EXPECTED_CARD_NOT_FOUND);
    }

    @Test
    void testWrapsNoColourSelectedExceptionAsUnoUserException() throws Exception {
        doThrow(new NoColourSelectedException(EXPECTED_NO_COLOUR_SELECTED))
                .when(controller).playCard(PLAYER_ID, CARD_ID, SELECTED_COLOUR);

        assertCommandException(EXPECTED_NO_COLOUR_SELECTED);
    }

    @Test
    void testWrapsNotAColourChangeCardExceptionAsUnoUserException() throws Exception {
        doThrow(new NotAColourChangeCardException(EXPECTED_NOT_A_COLOUR_CHANGE_CARD))
                .when(controller).playCard(PLAYER_ID, CARD_ID, SELECTED_COLOUR);

        assertCommandException(EXPECTED_NOT_A_COLOUR_CHANGE_CARD);
    }

    @Test
    void testWrapsPlayerNotFoundExceptionAsUnoUserException() throws Exception {
        doThrow(new PlayerNotFoundException(EXPECTED_PLAYER_NOT_FOUND))
                .when(controller).playCard(PLAYER_ID, CARD_ID, SELECTED_COLOUR);

        assertCommandException(EXPECTED_PLAYER_NOT_FOUND);
    }

    @Test
    void testWrapsCannotPlayCardExceptionAsUnoUserException() throws Exception {
        doThrow(new CannotPlayCardException(EXPECTED_CANNOT_PLAY_CARD))
                .when(controller).playCard(PLAYER_ID, CARD_ID, SELECTED_COLOUR);

        assertCommandException(EXPECTED_CANNOT_PLAY_CARD);
    }

    @Test
    void testWrapsGameNotStartedExceptionAsUnoUserException() throws Exception {
        doThrow(new GameNotStartedException(EXPECTED_GAME_NOT_STARTED))
                .when(controller).playCard(PLAYER_ID, CARD_ID, SELECTED_COLOUR);

        assertCommandException(EXPECTED_GAME_NOT_STARTED);
    }

    private void assertCommandException(String expectedMessage) {
        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new PlaySpecialCardCommand(controller, PLAYER_ID, CARD_ID, SELECTED_COLOUR).execute());

        assertEquals(expectedMessage, exception.getMessage());
        verify(controller, never()).nextTurn();
    }
}
