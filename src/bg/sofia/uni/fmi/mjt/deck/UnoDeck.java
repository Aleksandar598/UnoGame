package bg.sofia.uni.fmi.mjt.deck;

import bg.sofia.uni.fmi.mjt.card.Card;
import bg.sofia.uni.fmi.mjt.card.ColourChangingCard;
import bg.sofia.uni.fmi.mjt.card.EffectCard;
import bg.sofia.uni.fmi.mjt.card.Resettable;
import bg.sofia.uni.fmi.mjt.deck.creator.CardPileCreator;

import java.sql.ResultSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class UnoDeck implements Deck {

    private Deque<Card> pile;
    private Deque<Card> draw;

    public UnoDeck(CardPileCreator creator) {
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
        pile.add(card);
    }

    @Override
    public Card getLastPlayedCard() {
        return pile.getLast();
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
