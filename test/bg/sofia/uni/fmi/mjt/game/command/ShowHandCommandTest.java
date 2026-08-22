package bg.sofia.uni.fmi.mjt.game.command;

import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.game.card.Card;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShowHandCommandTest {
    private static final int PLAYER_ID = 7;
    private static final String FIRST_CARD = "RED FIVE";
    private static final String SECOND_CARD = "BLUE SKIP";
    private static final String EXPECTED_HAND = "0. " + FIRST_CARD + System.lineSeparator() +"1. " + SECOND_CARD + System.lineSeparator();
    private static final String EXPECTED_GAME_NOT_STARTED = "Game has not started yet";
    private static final String EXPECTED_PLAYER_NOT_IN_GAME = "Player not in game";

    private GameController controller;

    @BeforeEach
    void setUp() {
        controller = Mockito.mock(GameController.class);
    }

    @Test
    void testRejectsNullController() {
        assertThrows(IllegalArgumentException.class, () -> new ShowHandCommand(null, PLAYER_ID));
    }

    @Test
    void testReturnsNumberedHandWhenGameHasStarted() throws Exception {
        Card firstCard = Mockito.mock(Card.class);
        Card secondCard = Mockito.mock(Card.class);
        Player player = Mockito.mock(Player.class);
        when(controller.hasStarted()).thenReturn(true);
        when(controller.getPlayer(PLAYER_ID)).thenReturn(player);
        when(player.getCards()).thenReturn(List.of(firstCard, secondCard));
        when(firstCard.getCardAsString()).thenReturn(FIRST_CARD);
        when(secondCard.getCardAsString()).thenReturn(SECOND_CARD);

        String response = new ShowHandCommand(controller, PLAYER_ID).execute();

        assertEquals(EXPECTED_HAND, response);
        verify(controller).getPlayer(PLAYER_ID);
    }

    @Test
    void testRejectsShowingHandBeforeGameStarts() throws PlayerNotFoundException {
        when(controller.hasStarted()).thenReturn(false);

        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new ShowHandCommand(controller, PLAYER_ID).execute());

        assertEquals(EXPECTED_GAME_NOT_STARTED, exception.getMessage());
        verify(controller, never()).getPlayer(PLAYER_ID);
    }

    @Test
    void testWrapsPlayerNotFoundExceptionAsUnoUserException() throws Exception {
        when(controller.hasStarted()).thenReturn(true);
        when(controller.getPlayer(PLAYER_ID)).thenThrow(new PlayerNotFoundException("Unknown player"));

        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new ShowHandCommand(controller, PLAYER_ID).execute());

        assertEquals(EXPECTED_PLAYER_NOT_IN_GAME, exception.getMessage());
    }
}
