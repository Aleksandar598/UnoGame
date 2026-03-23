package bg.sofia.uni.fmi.mjt.deck;

import bg.sofia.uni.fmi.mjt.card.Card;
import bg.sofia.uni.fmi.mjt.card.CardColour;
import bg.sofia.uni.fmi.mjt.card.CardType;
import bg.sofia.uni.fmi.mjt.card.ChooseColourCard;
import bg.sofia.uni.fmi.mjt.deck.creator.CardPileCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UnoDeckTest {

    private static final Card FIRST_CARD = Card.of(CardColour.RED, CardType.ONE, 1);
    private static final Card SECOND_CARD = Card.of(CardColour.BLUE, CardType.TWO, 2);
    private static final Card THIRD_CARD = Card.of(CardColour.GREEN, CardType.THREE, 3);
    private static final Card FOURTH_CARD =  Card.of(CardColour.YELLOW, CardType.FOUR, 4);
    private static final Card DIFFERENT_CARD = Card.of(CardColour.RED, CardType.SKIP_MOVE_CARD, 5);
    private static final Card FIRST_PLAYED_CARD = Card.of(CardColour.YELLOW, CardType.THREE, 6);
    private static final Card SECOND_PLAYED_CARD = Card.of(CardColour.GREEN, CardType.TWO, 7);
    private static final Card THIRD_PLAYED_CARD  = Card.of(CardColour.BLUE, CardType.PLUS_TWO_CARD, 8);
    private static final Card FOURTH_PLAYED_CARD = Card.of(CardColour.RED, CardType.NINE, 9);

    private UnoDeck deck;

    @BeforeEach
    public void setUp() {
        Deque<Card> testCards = new ArrayDeque<>();
        testCards.add(FIRST_CARD);
        testCards.add(SECOND_CARD);
        testCards.add(THIRD_CARD);
        testCards.add(FOURTH_CARD);
        CardPileCreator creator = mock();
        when(creator.getDeck()).thenReturn(testCards);
        deck = new UnoDeck(creator);
    }

    @Test
    public void testConstructorNullCardPileCase() {
        assertThrows(IllegalArgumentException.class,
                () -> new UnoDeck(null));
    }

    @Test
    public void testFirstCardSetCorrectly() {
        assertEquals(FIRST_CARD, deck.getLastPlayedCard());
    }

    @Test
    public void testDrawCardNormalCase() {
        assertEquals(SECOND_CARD, deck.drawCard());
    }

    @Test
    public void testMultipleCardDraws() {
        assertAll(
                () -> assertEquals(SECOND_CARD, deck.drawCard(), "Draw SECOND_CARD"),
                () -> assertEquals(THIRD_CARD, deck.drawCard(), "Draw THIRD_CARD"),
                () -> assertEquals(FOURTH_CARD, deck.drawCard(), "Draw FOURTH_CARD")
        );
    }

    @Test
    public void testPlayCardNullCase() {
        assertThrows(IllegalArgumentException.class,
                () -> deck.playCard(null));
    }

    @Test
    public void testPlayCardOneCard() {
        deck.playCard(DIFFERENT_CARD);
        assertEquals(DIFFERENT_CARD, deck.getLastPlayedCard());
    }

    @Test
    public void testRecyclePile() {
        deck.playCard(FIRST_PLAYED_CARD);
        deck.playCard(SECOND_PLAYED_CARD);
        deck.playCard(THIRD_PLAYED_CARD);
        deck.playCard(FOURTH_PLAYED_CARD);

        deck.drawCard();
        deck.drawCard();
        deck.drawCard();
        assertEquals(THIRD_PLAYED_CARD, deck.drawCard());
    }

    @Test
    public void testResettableCardInRecyclePile() {
        ChooseColourCard card = mock();
        deck.playCard(FIRST_PLAYED_CARD);
        deck.playCard(card);
        deck.playCard(SECOND_PLAYED_CARD);

        deck.drawCard();
        deck.drawCard();
        deck.drawCard();
        deck.drawCard();

        verify(card).reset();
    }
}