package bg.sofia.uni.fmi.mjt.deck.creator;

import bg.sofia.uni.fmi.mjt.card.Card;
import bg.sofia.uni.fmi.mjt.card.CardColour;
import bg.sofia.uni.fmi.mjt.card.CardType;
import bg.sofia.uni.fmi.mjt.id.CardIdGenerator;
import org.junit.jupiter.api.Test;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class DeckCreatorTest {

    private static final int CARD_COUNT_OF_EACH_COLOUR = 25;
    private static final int CARD_COUNT_OF_ZEROES = 4;
    private static final int CARD_COUNT_OF_NORMAL_CARDS = 8;
    private static final int CARD_COUNT_OF_PLUS_TWO = 8;
    private static final int CARD_COUNT_OF_SKIP_TURN = 8;
    private static final int CARD_COUNT_OF_REVERSE_DIRECTION = 8;
    private static final int CARD_COUNT_OF_JOKERS = 4;



    @Test
    public void testConstructorNullIdGenerator() {
        assertThrows(IllegalArgumentException.class,
                () -> new DeckCreator(null));
    }

    @Test
    public void testGetDeckCardColourCountCorrectness() {
        Deque<Card> cards = new DeckCreator(new CardIdGenerator()).getDeck();
        Map<CardColour, Integer> cardColourCount = new HashMap<>();

        for (Card c : cards) {
            cardColourCount.put(c.getCardColour(), cardColourCount.getOrDefault(c.getCardColour(), 0) + 1);
        }
        assertAll(
                () -> assertEquals(CARD_COUNT_OF_EACH_COLOUR, cardColourCount.getOrDefault(CardColour.RED, 0), "REDS ERROR"),
                () -> assertEquals(CARD_COUNT_OF_EACH_COLOUR, cardColourCount.getOrDefault(CardColour.BLUE, 0), "BLUES ERROR"),
                () -> assertEquals(CARD_COUNT_OF_EACH_COLOUR, cardColourCount.getOrDefault(CardColour.GREEN, 0), "GREENS ERROR"),
                () -> assertEquals(CARD_COUNT_OF_EACH_COLOUR, cardColourCount.getOrDefault(CardColour.YELLOW, 0), "YELLOWS ERROR")
        );

    }

    @Test
    public void testGetDeckCardTypeCountCorrectness() {
        Deque<Card> cards = new DeckCreator(new CardIdGenerator()).getDeck();
        Map<CardType, Integer> cardTypeCount = new HashMap<>();
        for (Card c : cards) {
            cardTypeCount.put(c.getCardType(), cardTypeCount.getOrDefault(c.getCardType(), 0) + 1);
        }

        assertAll(
                () -> assertEquals(CARD_COUNT_OF_ZEROES, cardTypeCount.getOrDefault(CardType.ZERO, 0), "ZEROES ERROR"),
                () -> assertEquals(CARD_COUNT_OF_NORMAL_CARDS, cardTypeCount.getOrDefault(CardType.ONE, 0),"ONES ERROR"),
                () -> assertEquals(CARD_COUNT_OF_NORMAL_CARDS, cardTypeCount.getOrDefault(CardType.TWO, 0), "TWOS ERROR"),
                () -> assertEquals(CARD_COUNT_OF_NORMAL_CARDS, cardTypeCount.getOrDefault(CardType.THREE, 0), "THREES ERROR"),
                () -> assertEquals(CARD_COUNT_OF_NORMAL_CARDS, cardTypeCount.getOrDefault(CardType.FOUR, 0), "FOURS ERROR"),
                () -> assertEquals(CARD_COUNT_OF_NORMAL_CARDS, cardTypeCount.getOrDefault(CardType.FIVE, 0), "FIVES ERROR"),
                () -> assertEquals(CARD_COUNT_OF_NORMAL_CARDS, cardTypeCount.getOrDefault(CardType.SIX, 0), "SIXES ERROR"),
                () -> assertEquals(CARD_COUNT_OF_NORMAL_CARDS, cardTypeCount.getOrDefault(CardType.SEVEN, 0), "SEVENS ERROR"),
                () -> assertEquals(CARD_COUNT_OF_NORMAL_CARDS, cardTypeCount.getOrDefault(CardType.EIGHT, 0), "EIGHTS ERROR"),
                () -> assertEquals(CARD_COUNT_OF_NORMAL_CARDS, cardTypeCount.getOrDefault(CardType.NINE, 0), "NINES ERROR"),
                () -> assertEquals(CARD_COUNT_OF_PLUS_TWO, cardTypeCount.getOrDefault(CardType.PLUS_TWO_CARD, 0), "PLUS TWOS ERROR"),
                () -> assertEquals(CARD_COUNT_OF_REVERSE_DIRECTION, cardTypeCount.getOrDefault(CardType.SWITCH_CARD, 0), "REVERSE DIRECTION ERROR"),
                () -> assertEquals(CARD_COUNT_OF_SKIP_TURN, cardTypeCount.getOrDefault(CardType.SKIP_MOVE_CARD, 0), "SKIP TURN ERROR"),
                () -> assertEquals(CARD_COUNT_OF_JOKERS, cardTypeCount.getOrDefault(CardType.PLUS_FOUR_CARD, 0), "PLUS FOURS ERROR"),
                () -> assertEquals(CARD_COUNT_OF_JOKERS, cardTypeCount.getOrDefault(CardType.CHOOSE_COLOUR_CARD, 0), "CHOOSE COLOURS ERROR")
        );
    }
}