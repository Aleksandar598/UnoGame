package bg.sofia.uni.fmi.mjt.player;

import bg.sofia.uni.fmi.mjt.game.card.Card;
import bg.sofia.uni.fmi.mjt.game.card.CardColour;
import bg.sofia.uni.fmi.mjt.game.card.CardType;
import bg.sofia.uni.fmi.mjt.exception.CardNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnoPlayerTest {

    private UnoPlayer player;
    private static final String name = "Name";
    private static final int id = 1;
    private static final Card FIRST_CARD = Card.of(CardColour.RED, CardType.TWO, 1);
    private static final Card SECOND_CARD = Card.of(CardColour.BLUE, CardType.NINE, 2);
    private static final int NON_EXISTENT_CARD_ID = 2984;

    @BeforeEach
    void setUp() {
        player = new UnoPlayer(name, id);
    }

    @Test
    public void testConstructorNullName() {
        assertThrows(IllegalArgumentException.class,
                () -> new UnoPlayer(null, id));
    }

    @Test
    public void testAddCardNullCase() {
        assertThrows(IllegalArgumentException.class,
                () -> player.addCard(null));
    }

    @Test
    public void testAddCardNormalCase() {
        player.addCard(FIRST_CARD);
        assertEquals(List.of(FIRST_CARD), player.getCards());
    }

    @Test
    public void testPlayCardNotExistentCard() {
        player.addCard(FIRST_CARD);
        assertThrows(CardNotFoundException.class,
                () -> player.playCard(NON_EXISTENT_CARD_ID));
    }

    @Test
    public void testPlayCardNormalCase() throws CardNotFoundException {
        player.addCard(FIRST_CARD);
        assertEquals(FIRST_CARD, player.playCard(1));
    }

    @Test
    public void testPlayCardRemovePlayedCard() throws CardNotFoundException {
        player.addCard(FIRST_CARD);
        player.playCard(1);
        assertEquals(new ArrayList<>(), player.getCards());
    }

    @Test
    public void testHasUnoCorrectness() {
        player.addCard(FIRST_CARD);
        assertTrue(player.hasUno());
    }

    @Test
    public void testHasUnoFalseCase() {
        player.addCard(FIRST_CARD);
        player.addCard(SECOND_CARD);
        assertFalse(player.hasUno());
    }

    @Test
    public void testSayUnoCantSayUno() {
        player.addCard(FIRST_CARD);
        player.addCard(SECOND_CARD);
        assertThrows(UnoUserException.class,
                () -> player.sayUno());
    }

    @Test
    public void testSayUnoCorrectness() throws UnoUserException {
        player.addCard(FIRST_CARD);
        player.sayUno();
        assertTrue(player.hasSaidUno());
    }

    @Test
    public void testResetUnoStatus() throws UnoUserException {
        player.addCard(FIRST_CARD);
        player.sayUno();
        player.resetUnoStatus();
        assertFalse(player.hasSaidUno());
    }

    @Test
    public void testHasWonCorrectness() {
        player.setHasWon(true);
        assertTrue(player.hasWon());
    }
}