package bg.sofia.uni.fmi.mjt.game.deck;

import bg.sofia.uni.fmi.mjt.game.card.Card;

public interface Deck {

    Card drawCard();

    void playCard(Card card);

    Card getLastPlayedCard();

}
