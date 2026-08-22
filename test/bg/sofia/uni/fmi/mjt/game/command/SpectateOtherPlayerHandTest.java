package bg.sofia.uni.fmi.mjt.game.command;

import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.game.card.Card;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.player.PlayerStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SpectateOtherPlayerHandTest {
    private static final int SPECTATOR_ID = 7;
    private static final int TARGET_PLAYER_ID = 12;
    private static final String FIRST_CARD = "RED FIVE";
    private static final String SECOND_CARD = "BLUE DRAW TWO";
    private static final String EXPECTED_TARGET_HAND = "1. " + FIRST_CARD + System.lineSeparator()
            + "2. " + SECOND_CARD + System.lineSeparator();
    private static final String EXPECTED_GAME_NOT_STARTED = "Game has not started yet";
    private static final String EXPECTED_CANNOT_SPECTATE = "You cannot spectate";
    private static final String EXPECTED_TARGET_NOT_PLAYING = "Player is not playing";
    private static final String EXPECTED_PLAYER_NOT_FOUND = "Player not found";

    private GameController controller;

    @BeforeEach
    void setUp() {
        controller = Mockito.mock(GameController.class);
    }

    @Test
    void testRejectsNullController() {
        assertThrows(IllegalArgumentException.class,
                () -> new SpectateOtherPlayerHand(null, SPECTATOR_ID, TARGET_PLAYER_ID));
    }

    @Test
    void testReturnsNumberedHandForSpectator() throws Exception {
        Player spectator = Mockito.mock(Player.class);
        Player targetPlayer = Mockito.mock(Player.class);
        Card firstCard = Mockito.mock(Card.class);
        Card secondCard = Mockito.mock(Card.class);
        when(controller.hasStarted()).thenReturn(true);
        when(controller.getPlayer(SPECTATOR_ID)).thenReturn(spectator);
        when(spectator.getPlayerStatus()).thenReturn(PlayerStatus.SPECTATING);
        when(controller.getPlayer(TARGET_PLAYER_ID)).thenReturn(targetPlayer);
        when(targetPlayer.getPlayerStatus()).thenReturn(PlayerStatus.PLAYING);
        when(targetPlayer.getCards()).thenReturn(List.of(firstCard, secondCard));
        when(firstCard.getCardAsString()).thenReturn(FIRST_CARD);
        when(secondCard.getCardAsString()).thenReturn(SECOND_CARD);

        String response = new SpectateOtherPlayerHand(controller, SPECTATOR_ID, TARGET_PLAYER_ID).execute();

        assertEquals(EXPECTED_TARGET_HAND, response);
        verify(targetPlayer).getCards();
    }

    @Test
    void testRejectsSpectatingHandBeforeGameStarts() throws PlayerNotFoundException {
        when(controller.hasStarted()).thenReturn(false);

        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new SpectateOtherPlayerHand(controller, SPECTATOR_ID, TARGET_PLAYER_ID).execute());

        assertEquals(EXPECTED_GAME_NOT_STARTED, exception.getMessage());
        verify(controller, never()).getPlayer(SPECTATOR_ID);
    }

    @Test
    void testRejectsPlayerWhoIsNotSpectating() throws Exception {
        Player player = Mockito.mock(Player.class);
        when(controller.hasStarted()).thenReturn(true);
        when(controller.getPlayer(SPECTATOR_ID)).thenReturn(player);
        when(player.getPlayerStatus()).thenReturn(PlayerStatus.PLAYING);

        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new SpectateOtherPlayerHand(controller, SPECTATOR_ID, TARGET_PLAYER_ID).execute());

        assertEquals(EXPECTED_CANNOT_SPECTATE, exception.getMessage());
        verify(controller, never()).getPlayer(TARGET_PLAYER_ID);
    }

    @Test
    void testRejectsTargetPlayerWhoIsNotPlaying() throws Exception {
        Player spectator = Mockito.mock(Player.class);
        Player targetPlayer = Mockito.mock(Player.class);
        when(controller.hasStarted()).thenReturn(true);
        when(controller.getPlayer(SPECTATOR_ID)).thenReturn(spectator);
        when(spectator.getPlayerStatus()).thenReturn(PlayerStatus.SPECTATING);
        when(controller.getPlayer(TARGET_PLAYER_ID)).thenReturn(targetPlayer);
        when(targetPlayer.getPlayerStatus()).thenReturn(PlayerStatus.SPECTATING);

        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new SpectateOtherPlayerHand(controller, SPECTATOR_ID, TARGET_PLAYER_ID).execute());

        assertEquals(EXPECTED_TARGET_NOT_PLAYING, exception.getMessage());
        verify(targetPlayer, never()).getCards();
    }

    @Test
    void testWrapsMissingSpectatorAsUnoUserException() throws Exception {
        when(controller.hasStarted()).thenReturn(true);
        when(controller.getPlayer(SPECTATOR_ID)).thenThrow(new PlayerNotFoundException("Missing spectator"));

        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new SpectateOtherPlayerHand(controller, SPECTATOR_ID, TARGET_PLAYER_ID).execute());

        assertEquals(EXPECTED_PLAYER_NOT_FOUND, exception.getMessage());
    }

    @Test
    void testWrapsMissingTargetPlayerAsUnoUserException() throws Exception {
        Player spectator = Mockito.mock(Player.class);
        when(controller.hasStarted()).thenReturn(true);
        when(controller.getPlayer(SPECTATOR_ID)).thenReturn(spectator);
        when(spectator.getPlayerStatus()).thenReturn(PlayerStatus.SPECTATING);
        when(controller.getPlayer(TARGET_PLAYER_ID)).thenThrow(new PlayerNotFoundException("Missing target"));

        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> new SpectateOtherPlayerHand(controller, SPECTATOR_ID, TARGET_PLAYER_ID).execute());

        assertEquals(EXPECTED_PLAYER_NOT_FOUND, exception.getMessage());
    }
}
