package bg.sofia.uni.fmi.mjt;

import bg.sofia.uni.fmi.mjt.card.Card;
import bg.sofia.uni.fmi.mjt.card.CardColour;
import bg.sofia.uni.fmi.mjt.exception.CardNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.player.Player;

public interface GameController {

    void skipNextPlayer();

    void removePlayer(int playerId) throws PlayerNotFoundException;

    void addCardForDraw(int count);

    void reversePlayerDirection();

    void setColour(CardColour colour);

    Player getPlayer(int playerId) throws PlayerNotFoundException;

    Player getCurrentPlayer();

    void nextTurn();

    Card getTopCard();

    CardColour getCurrentColour();

    int penaltyCardCount();

    boolean isTherePendingCardDraw();

    Card drawCard(int playerId) throws PlayerNotFoundException;

    void acceptPenalty(int playerId) throws PlayerNotFoundException;

    void resetPenalty();

    void playCard(int playerId, int cardId) throws CardNotFoundException, PlayerNotFoundException;

    void playCard(int playerId, int cardId, CardColour colour) throws CardNotFoundException, PlayerNotFoundException;

    boolean checkPlayerCanPlayAnyCards(int playerId);
}
