package bg.sofia.uni.fmi.mjt.deck;

import bg.sofia.uni.fmi.mjt.card.Card;

public interface Deck {

    Card drawCard();

    void playCard(Card card);

    Card getLastPlayedCard();

}
