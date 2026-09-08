package bg.sofia.uni.fmi.mjt.game.command;

import bg.sofia.uni.fmi.mjt.command.gameCommand.SpectateCommand;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.player.PlayerStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SpectateCommandTest {
    private static final int PLAYER_ID = 7;
    private static final String EXPECTED_SUCCESSFUL_SPECTATE = "Now spectating";
    private static final String EXPECTED_GAME_NOT_STARTED = "Game has not started yet";
    private static final String EXPECTED_PLAYER_STILL_PLAYING = "You are still in the game!";
    private static final String EXPECTED_PLAYER_NOT_IN_GAME = "You are not in the game";

    private GameController controller;

    @BeforeEach
    void setUp() {
        controller = Mockito.mock(GameController.class);
    }

    @Test
    void testRejectsNullController() {
        assertThrows(IllegalArgumentException.class, () -> new SpectateCommand(null, PLAYER_ID));
    }

    @Test
    void testSetsWinnerToSpectatingAndReturnsSuccess() throws Exception {
        Player player = Mockito.mock(Player.class);
        when(controller.hasStarted()).thenReturn(true);
        when(controller.getPlayer(PLAYER_ID)).thenReturn(player);
        when(player.hasWon()).thenReturn(true);

        String response = new SpectateCommand(controller, PLAYER_ID).execute();

        assertEquals(EXPECTED_SUCCESSFUL_SPECTATE, response);
        verify(player).setPlayerStatus(PlayerStatus.SPECTATING);
    }

    @Test
    void testRejectsSpectatingBeforeGameStarts() throws PlayerNotFoundException {
        when(controller.hasStarted()).thenReturn(false);

        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new SpectateCommand(controller, PLAYER_ID).execute());

        assertEquals(EXPECTED_GAME_NOT_STARTED, exception.getMessage());
        verify(controller, never()).getPlayer(PLAYER_ID);
    }

    @Test
    void testRejectsSpectatingWhilePlayerHasNotWon() throws Exception {
        Player player = Mockito.mock(Player.class);
        when(controller.hasStarted()).thenReturn(true);
        when(controller.getPlayer(PLAYER_ID)).thenReturn(player);
        when(player.hasWon()).thenReturn(false);

        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new SpectateCommand(controller, PLAYER_ID).execute());

        assertEquals(EXPECTED_PLAYER_STILL_PLAYING, exception.getMessage());
        verify(player, never()).setPlayerStatus(PlayerStatus.SPECTATING);
    }

    @Test
    void testWrapsPlayerNotFoundExceptionAsUnoUserException() throws Exception {
        when(controller.hasStarted()).thenReturn(true);
        when(controller.getPlayer(PLAYER_ID)).thenThrow(new PlayerNotFoundException("Unknown player"));

        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new SpectateCommand(controller, PLAYER_ID).execute());

        assertEquals(EXPECTED_PLAYER_NOT_IN_GAME, exception.getMessage());
    }
}
