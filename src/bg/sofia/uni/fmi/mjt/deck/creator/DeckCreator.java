package bg.sofia.uni.fmi.mjt.deck.creator;

import bg.sofia.uni.fmi.mjt.card.Card;
import bg.sofia.uni.fmi.mjt.card.CardColour;
import bg.sofia.uni.fmi.mjt.card.CardType;
import bg.sofia.uni.fmi.mjt.id.IdGenerator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class DeckCreator implements CardPileCreator {
    private IdGenerator generator;
    private static final int FIRST_DIGIT_OF_CARDS = 1;
    private static final int LAST_DIGIT_OF_CARDS = 9;
    private static final int COUNT_OF_EACH_NORMAL_CARD = 2;
    private static final int COUNT_OF_EFFECT_COLORED_CARDS = 2;
    private static final int COUNT_OF_NO_COLOR_SPECIAL_CARDS = 4;

    public DeckCreator(IdGenerator generator) {
        if (generator == null) {
            throw new IllegalArgumentException("generator cannot be null");
        }
        this.generator = generator;
    }

    @Override
    public Deque<Card> getDeck() {
        List<Card> cards = createDeck();
        Collections.shuffle(cards);
        Deque<Card> deck = new ArrayDeque<>(cards);
        return deck;
    }

    private List<Card> createDeck() {
        List<Card> cards = new ArrayList<>();

        for (CardColour c : CardColour.getPlayableColours()) {
            addColoredCards(cards, c);
        }
        addJokers(cards);
        return cards;
    }

    private void addColoredCards(List<Card> cards, CardColour colour) {

        cards.add(Card.of(colour, CardType.ZERO, generator.getId()));

        for (int i = FIRST_DIGIT_OF_CARDS; i <= LAST_DIGIT_OF_CARDS; i++) {
            for (int j = 0; j < COUNT_OF_EACH_NORMAL_CARD; j++ ) {
                cards.add(Card.of(colour, CardType.fromInt(i), generator.getId()));
            }
        }
        for (int i = 0; i < COUNT_OF_EFFECT_COLORED_CARDS; i++) {
            cards.add(Card.of(colour, CardType.SWITCH_CARD, generator.getId()));
            cards.add(Card.of(colour, CardType.PLUS_TWO_CARD, generator.getId()));
            cards.add(Card.of(colour, CardType.SKIP_MOVE_CARD, generator.getId()));
        }
    }

    private void addJokers(List<Card> cards) {
        for (int i = 0; i < COUNT_OF_NO_COLOR_SPECIAL_CARDS; i++) {
            cards.add(Card.of(CardColour.SPECIAL, CardType.CHOOSE_COLOUR_CARD, generator.getId()));
            cards.add(Card.of(CardColour.SPECIAL, CardType.PLUS_FOUR_CARD, generator.getId()));
        }
    }
}
