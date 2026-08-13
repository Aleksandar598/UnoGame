package bg.sofia.uni.fmi.mjt.game.controller;

import bg.sofia.uni.fmi.mjt.game.card.Card;
import bg.sofia.uni.fmi.mjt.game.card.CardColour;
import bg.sofia.uni.fmi.mjt.exception.*;
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

    boolean hasStarted();

    int penaltyCardCount();

    boolean isTherePendingCardDraw();

    Card drawCard(int playerId) throws PlayerNotFoundException;

    String acceptPenalty(int playerId) throws PlayerNotFoundException;

    void playCard(int playerId, int cardId) throws CardNotFoundException, PlayerNotFoundException, NoColourSelectedException, CannotPlayCardException;

    void playCard(int playerId, int cardId, CardColour colour) throws CardNotFoundException, PlayerNotFoundException, NotAColourChangeCardException, NoColourSelectedException, CannotPlayCardException;

    boolean checkPlayerCanPlayAnyCards(int playerId) throws PlayerNotFoundException;

    void addPlayer(Player player) throws GameHasStartedException, MaximumPlayerCountReached;

    String getPlayedCardsLog();

    String getWinnerLog();

    Player getCreator() throws PlayerNotFoundException;

    void startGame() throws CannotStartGameException;
}
