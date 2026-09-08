package bg.sofia.uni.fmi.mjt.game.command;

import bg.sofia.uni.fmi.mjt.command.gameCommand.GetCurrentColourCommand;
import bg.sofia.uni.fmi.mjt.game.card.CardColour;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetCurrentColourCommandTest {
    private static final CardColour CURRENT_COLOUR = CardColour.BLUE;
    private static final String EXPECTED_CURRENT_COLOUR = "BLUE";

    private GameController controller;

    @BeforeEach
    void setUp() {
        controller = Mockito.mock(GameController.class);
    }

    @Test
    void testRejectsNullController() {
        assertThrows(IllegalArgumentException.class, () -> new GetCurrentColourCommand(null));
    }

    @Test
    void testReturnsCurrentColourAsString() throws Exception {
        when(controller.getCurrentColour()).thenReturn(CURRENT_COLOUR);

        String response = new GetCurrentColourCommand(controller).execute();

        assertEquals(EXPECTED_CURRENT_COLOUR, response);
        verify(controller).getCurrentColour();
    }
}
