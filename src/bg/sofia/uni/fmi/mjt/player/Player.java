package bg.sofia.uni.fmi.mjt.player;

import bg.sofia.uni.fmi.mjt.card.Card;
import bg.sofia.uni.fmi.mjt.exception.CardNotFoundException;

import java.util.List;

public interface Player {

    String getName();

    void addCard(Card card);

    List<Card> getCards();

    Card playCard(int cardId) throws CardNotFoundException;

    boolean hasUno();

    void sayUno();

    boolean hasSaidUno();

    void resetUnoStatus();

    boolean hasWon();

    void setHasWon(boolean hasWon);

    void setPlayerStatus(PlayerStatus status);

    PlayerStatus getPlayerStatus();

    int getId();
}
