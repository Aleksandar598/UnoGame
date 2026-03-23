package bg.sofia.uni.fmi.mjt.logger;

import bg.sofia.uni.fmi.mjt.card.Card;
import bg.sofia.uni.fmi.mjt.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameLoggerTest {

    private GameLogger logger;
    private Card card;
    private Player player;

    @BeforeEach
    public void setUp() {
        logger = new GameLogger();
        card = mock();
        player = mock();
    }

    @Test
    public void testLogCardNullCardCase() {
        assertThrows(IllegalArgumentException.class,
                () -> logger.logCard(null, player));
    }

    @Test
    public void testLogCardNullPlayerCase() {
        assertThrows(IllegalArgumentException.class,
                () -> logger.logCard(card, null));
    }

    @Test
    public void testLogCardNormalCase() {
        when(player.getName()).thenReturn("Name");
        when(card.getCardAsString()).thenReturn("Card");
        logger.logCard(card, player);
        assertEquals("1. Name: Card" + System.lineSeparator(), logger.getCardLog());
    }

    @Test
    public void testLogWinnerNullCase() {
        assertThrows(IllegalArgumentException.class,
                () -> logger.logWinner(null));
    }

    @Test
    public void testLogWinnerNormalCase() {
        when(player.getName()).thenReturn("Name");
        logger.logWinner(player);
        assertEquals("1. Name" + System.lineSeparator(), logger.getWinnerLog());
    }
}