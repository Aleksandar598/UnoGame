package bg.sofia.uni.fmi.mjt.game.controller;

import bg.sofia.uni.fmi.mjt.exception.CannotPlayCardException;
import bg.sofia.uni.fmi.mjt.exception.CannotStartGameException;
import bg.sofia.uni.fmi.mjt.exception.CardNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.GameHasStartedException;
import bg.sofia.uni.fmi.mjt.exception.GameNotStartedException;
import bg.sofia.uni.fmi.mjt.exception.InvalidActionException;
import bg.sofia.uni.fmi.mjt.exception.MaximumPlayerCountReached;
import bg.sofia.uni.fmi.mjt.exception.NoColourSelectedException;
import bg.sofia.uni.fmi.mjt.exception.NotAColourChangeCardException;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.game.card.Card;
import bg.sofia.uni.fmi.mjt.game.card.CardColour;
import bg.sofia.uni.fmi.mjt.player.Player;

public interface GameController {

    void skipNextPlayer();

    void removePlayer(int playerId) throws PlayerNotFoundException;

    void addCardForDraw(int count);

    void reversePlayerDirection();

    void setColour(CardColour colour) throws NoColourSelectedException;

    Player getPlayer(int playerId) throws PlayerNotFoundException;

    Player getCurrentPlayer();

    void nextTurn();

    Card getTopCard();

    CardColour getCurrentColour();

    boolean hasStarted();

    int penaltyCardCount();

    boolean isTherePendingCardDraw();

    Card drawCard(int playerId) throws PlayerNotFoundException, CannotPlayCardException,
            UnoUserException, GameNotStartedException, InvalidActionException;

    String acceptPenalty(int playerId) throws PlayerNotFoundException,
            CannotPlayCardException, UnoUserException;

    String playCard(int playerId, int cardId) throws CardNotFoundException, PlayerNotFoundException,
            NoColourSelectedException, CannotPlayCardException, UnoUserException;

    String playCard(int playerId, int cardId, CardColour colour) throws CardNotFoundException,
            PlayerNotFoundException,
            NoColourSelectedException, CannotPlayCardException, UnoUserException,
            GameNotStartedException, NotAColourChangeCardException;

    boolean checkPlayerCanPlayAnyCards(int playerId) throws PlayerNotFoundException;

    void addPlayer(Player player) throws GameHasStartedException, MaximumPlayerCountReached;

    String getPlayedCardsLog();

    String getWinnerLog();

    boolean hasEnded();

    void startGame() throws CannotStartGameException;
}
