package bg.sofia.uni.fmi.mjt.game.command;

import bg.sofia.uni.fmi.mjt.command.gameCommand.ShowPlayedCardsCommand;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShowPlayedCardsCommandTest {
    private static final String PLAYED_CARDS_LOG = "RED FIVE" + System.lineSeparator() + "BLUE SKIP" + System.lineSeparator();
    private static final String EXPECTED_PLAYED_CARDS_LOG = PLAYED_CARDS_LOG;
    private static final String EXPECTED_GAME_NOT_STARTED = "Game has not started yet";

    private GameController controller;

    @BeforeEach
    void setUp() {
        controller = Mockito.mock(GameController.class);
    }

    @Test
    void testRejectsNullController() {
        assertThrows(IllegalArgumentException.class, () -> new ShowPlayedCardsCommand(null));
    }

    @Test
    void testReturnsPlayedCardsLogWhenGameHasStarted() throws Exception {
        when(controller.hasStarted()).thenReturn(true);
        when(controller.getPlayedCardsLog()).thenReturn(PLAYED_CARDS_LOG);

        String response = new ShowPlayedCardsCommand(controller).execute();

        assertEquals(EXPECTED_PLAYED_CARDS_LOG, response);
        verify(controller).getPlayedCardsLog();
    }

    @Test
    void testRejectsShowingPlayedCardsBeforeGameStarts() {
        when(controller.hasStarted()).thenReturn(false);

        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new ShowPlayedCardsCommand(controller).execute());

        assertEquals(EXPECTED_GAME_NOT_STARTED, exception.getMessage());
        verify(controller, never()).getPlayedCardsLog();
    }
}
