package bg.sofia.uni.fmi.mjt.game.deck.creator;

import bg.sofia.uni.fmi.mjt.game.card.Card;

import java.util.Deque;

public interface CardPileCreator {

    Deque<Card> getDeck();

}
