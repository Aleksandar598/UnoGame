package bg.sofia.uni.fmi.mjt.game.command;

import bg.sofia.uni.fmi.mjt.command.gameCommand.PlayNormalCardCommand;
import bg.sofia.uni.fmi.mjt.exception.CannotPlayCardException;
import bg.sofia.uni.fmi.mjt.exception.CardNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.NoColourSelectedException;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
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

class PlayNormalCardCommandTest {
    private static final int PLAYER_ID = 7;
    private static final int CARD_ID = 12;
    private static final String EXPECTED_SUCCESSFUL_PLAY = "Success";
    private static final String EXPECTED_CARD_NOT_FOUND = "No such card in your deck";
    private static final String EXPECTED_PLAYER_NOT_FOUND = "player not found";
    private static final String EXPECTED_NO_COLOUR_SELECTED = "Card needs to have colour selected";
    private static final String EXPECTED_CANNOT_PLAY_CARD = "Cannot play card";

    private GameController controller;

    @BeforeEach
    void setUp() {
        controller = Mockito.mock(GameController.class);
    }

    @Test
    void testRejectsNullController() {
        assertThrows(IllegalArgumentException.class, () -> new PlayNormalCardCommand(null, PLAYER_ID, CARD_ID));
    }

    @Test
    void testPlaysCardAdvancesTurnAndReturnsSuccess() throws Exception {
        when(controller.playCard(PLAYER_ID, CARD_ID)).thenReturn(EXPECTED_SUCCESSFUL_PLAY);
        String response = new PlayNormalCardCommand(controller, PLAYER_ID, CARD_ID).execute();

        assertEquals(EXPECTED_SUCCESSFUL_PLAY, response);
        verify(controller).playCard(PLAYER_ID, CARD_ID);
        verify(controller).nextTurn();
    }

    @Test
    void testWrapsCardNotFoundExceptionAsUnoUserException() throws Exception {
        doThrow(new CardNotFoundException("Card is missing"))
                .when(controller).playCard(PLAYER_ID, CARD_ID);
        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new PlayNormalCardCommand(controller, PLAYER_ID, CARD_ID).execute());

        assertEquals(EXPECTED_CARD_NOT_FOUND, exception.getMessage());
        verify(controller, never()).nextTurn();
    }

    @Test
    void testWrapsNoColourSelectedExceptionAsUnoUserException() throws Exception {
        doThrow(new NoColourSelectedException("No colour"))
                .when(controller).playCard(PLAYER_ID, CARD_ID);

        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new PlayNormalCardCommand(controller, PLAYER_ID, CARD_ID).execute());

        assertEquals(EXPECTED_NO_COLOUR_SELECTED, exception.getMessage());
        verify(controller, never()).nextTurn();
    }

    @Test
    void testWrapsCannotPlayCardExceptionAsUnoUserException() throws Exception {
        doThrow(new CannotPlayCardException("Not playable"))
                .when(controller).playCard(PLAYER_ID, CARD_ID);

        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new PlayNormalCardCommand(controller, PLAYER_ID, CARD_ID).execute());

        assertEquals(EXPECTED_CANNOT_PLAY_CARD, exception.getMessage());
        verify(controller, never()).nextTurn();
    }

    @Test
    void testWrapsPlayerNotFoundExceptionAsUnoUserException() throws Exception {
        doThrow(new PlayerNotFoundException("Player is missing"))
                .when(controller).playCard(PLAYER_ID, CARD_ID);

        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new PlayNormalCardCommand(controller, PLAYER_ID, CARD_ID).execute());

        assertEquals(EXPECTED_PLAYER_NOT_FOUND, exception.getMessage());
        verify(controller, never()).nextTurn();
    }
}
