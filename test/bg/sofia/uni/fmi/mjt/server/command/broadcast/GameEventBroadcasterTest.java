package bg.sofia.uni.fmi.mjt.server.command.broadcast;

import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.server.game.GameManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class GameEventBroadcasterTest {
    private static final String PLAYER_NAME = "user";
    private static final String PLAYED_CARD_MESSAGE = "BLUE FIVE";
    private static final String RESULT_MESSAGE = PLAYER_NAME + " played BLUE FIVE";
    private static final String EXPECTED_DRAW_MESSAGE = PLAYER_NAME + " drew a card";
    private static final String PLAY_COMMAND = "play --card-id=12";
    private static final String PLAY_CHOOSE_COMMAND = "play-choose --card-id=36 --color=blue";
    private static final String PLAY_PLUS_FOUR_COMMAND = "play-plus-four --card-id=29 --color=red";
    private static final String DRAW_COMMAND = "draw";
    private static final String PRIVATE_COMMAND = "show-hand";

    private GameManager gameManager;
    private Player player;

    @BeforeEach
    void setUp() {
        gameManager = Mockito.mock(GameManager.class);
        player = Mockito.mock(Player.class);
        when(player.getName()).thenReturn(PLAYER_NAME);
    }

    @Test
    void testBroadcastsNormalPlayedCardMessage() {
        when(player.getName()).thenReturn(PLAYER_NAME);
        GameEventBroadcaster.broadcast(PLAYED_CARD_MESSAGE, gameManager, PLAY_COMMAND, player);

        verify(gameManager).notifyAllInAGame(player, RESULT_MESSAGE);
    }

    @Test
    void testBroadcastsChooseColourPlayedCardMessage() {
        GameEventBroadcaster.broadcast(PLAYED_CARD_MESSAGE, gameManager, PLAY_CHOOSE_COMMAND, player);

        verify(gameManager).notifyAllInAGame(player, RESULT_MESSAGE);
    }

    @Test
    void testBroadcastsPlusFourPlayedCardMessage() {
        GameEventBroadcaster.broadcast(PLAYED_CARD_MESSAGE, gameManager, PLAY_PLUS_FOUR_COMMAND, player);

        verify(gameManager).notifyAllInAGame(player, RESULT_MESSAGE);
    }

    @Test
    void testBroadcastsDrawMessageWithPlayerName() {

        GameEventBroadcaster.broadcast(PLAYED_CARD_MESSAGE, gameManager, DRAW_COMMAND, player);

        verify(gameManager).notifyAllInAGame(player, EXPECTED_DRAW_MESSAGE);
    }

    @Test
    void testDoesNotBroadcastPrivateGameCommand() {
        GameEventBroadcaster.broadcast(PLAYED_CARD_MESSAGE, gameManager, PRIVATE_COMMAND, player);

        verifyNoInteractions(gameManager);
    }
}
