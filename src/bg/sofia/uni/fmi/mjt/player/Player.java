package bg.sofia.uni.fmi.mjt.player;

import bg.sofia.uni.fmi.mjt.game.card.Card;
import bg.sofia.uni.fmi.mjt.exception.CardNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;

import java.util.List;

public interface Player {

    String getName();

    void addCard(Card card);

    List<Card> getCards();

    Card playCard(int cardId) throws CardNotFoundException;

    boolean hasUno();

    void sayUno() throws UnoUserException;

    boolean hasSaidUno();

    void resetUnoStatus();

    boolean hasWon();

    void setHasWon(boolean hasWon);

    void setPlayerStatus(PlayerStatus status);

    PlayerStatus getPlayerStatus();

    int getId();
}
