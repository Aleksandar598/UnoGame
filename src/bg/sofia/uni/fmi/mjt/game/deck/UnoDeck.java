package bg.sofia.uni.fmi.mjt.game.deck;

import bg.sofia.uni.fmi.mjt.game.card.Card;
import bg.sofia.uni.fmi.mjt.game.card.Resettable;
import bg.sofia.uni.fmi.mjt.game.deck.creator.CardPileCreator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class UnoDeck implements Deck {

    private Deque<Card> pile;
    private Deque<Card> draw;

    public UnoDeck(CardPileCreator creator) {

        if (creator == null) {
            throw new IllegalArgumentException("creator cannot be null");
        }

        this.pile = new ArrayDeque<>();
        this.draw = creator.getDeck();
        pile.add(draw.pop());
    }

    @Override
    public Card drawCard() {
        if (draw.isEmpty()) {
            recyclePile();
        }
        return draw.pop();
    }

    @Override
    public void playCard(Card card) {

        if (card == null) {
            throw new IllegalArgumentException("card cannot be null");
        }
        pile.add(card);
    }

    @Override
    public Card getLastPlayedCard() {
        return pile.getLast();
    }

    @Override
    public void returnCards(List<Card> cards) {
        if (cards == null) {
            throw new IllegalArgumentException("cards cannot be null");
        }

        List<Card> returnedCards = new ArrayList<>(cards);
        Collections.shuffle(returnedCards);
        draw.addAll(returnedCards);
    }

    private void recyclePile() {
        Card topCard = pile.removeLast();

        List<Card> temp = new ArrayList<>(pile);
        Collections.reverse(temp);
        draw = new ArrayDeque<>(temp);

        pile = new ArrayDeque<>();
        pile.add(topCard);

        for (Card c : draw) {
            if (c instanceof Resettable) {
                ((Resettable) c).reset();
            }
        }
    }
}
