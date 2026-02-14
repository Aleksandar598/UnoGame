package bg.sofia.uni.fmi.mjt.controller;

import bg.sofia.uni.fmi.mjt.card.Card;
import bg.sofia.uni.fmi.mjt.card.CardColour;
import bg.sofia.uni.fmi.mjt.exception.CannotStartGameException;
import bg.sofia.uni.fmi.mjt.exception.CardNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.GameHasStartedException;
import bg.sofia.uni.fmi.mjt.exception.MaximumPlayerCountReached;
import bg.sofia.uni.fmi.mjt.exception.NoColourSelectedException;
import bg.sofia.uni.fmi.mjt.exception.NotAColourChangeCardException;
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

    boolean hasStarted();

    int penaltyCardCount();

    boolean isTherePendingCardDraw();

    Card drawCard(int playerId) throws PlayerNotFoundException;

    String acceptPenalty(int playerId) throws PlayerNotFoundException;

    void playCard(int playerId, int cardId) throws CardNotFoundException, PlayerNotFoundException, NoColourSelectedException;

    void playCard(int playerId, int cardId, CardColour colour) throws CardNotFoundException, PlayerNotFoundException, NotAColourChangeCardException, NoColourSelectedException;

    boolean checkPlayerCanPlayAnyCards(int playerId) throws PlayerNotFoundException;

    void addPlayer(Player player) throws GameHasStartedException, MaximumPlayerCountReached;

    String getPlayedCardsLog();

    String getWinnerLog();

    Player getCreator() throws PlayerNotFoundException;

    void startGame() throws CannotStartGameException;
}
