package bg.sofia.uni.fmi.mjt.deck.creator;

import bg.sofia.uni.fmi.mjt.card.Card;

import java.util.Deque;

public interface CardPileCreator {

    Deque<Card> getDeck();

}
