package bg.sofia.uni.fmi.mjt.player;

import bg.sofia.uni.fmi.mjt.card.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class UnoPlayer implements Player {

    List<Card> hand;
    String name;
    boolean hasSaidUno;

    UnoPlayer(String name) {
        this.name = name;
        this.hasSaidUno = false;
        this.hand = new ArrayList<>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("card cannot be null");
        }
        hand.add(card);
    }

    @Override
    public List<Card> getCards() {
        return Collections.unmodifiableList(hand);
    }

    @Override
    public Card playCard(int cardId) {

        for (Card c : this.hand) {
            if (c.getCardID() == cardId) {
                hand.remove(c);
                return c;
            }
        }
        throw new NoSuchElementException("hand does not contain such element");
    }

    @Override
    public boolean hasUno() {
        return this.hand.size() == 1;
    }

    @Override
    public void sayUno() {
        this.hasSaidUno = true;
    }

    @Override
    public boolean hasSaidUno() {
        return this.hasSaidUno;
    }

    @Override
    public void resetUnoStatus() {
        this.hasSaidUno = false;
    }
}
