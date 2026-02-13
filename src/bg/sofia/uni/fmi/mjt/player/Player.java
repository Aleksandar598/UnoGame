package bg.sofia.uni.fmi.mjt.player;

import bg.sofia.uni.fmi.mjt.card.Card;

import java.util.List;

public interface Player {

    String getName();

    void addCard(Card card);

    List<Card> getCards();

    Card playCard(int cardId);

    boolean hasUno();

    void sayUno();

    boolean hasSaidUno();

    void resetUnoStatus();
}
