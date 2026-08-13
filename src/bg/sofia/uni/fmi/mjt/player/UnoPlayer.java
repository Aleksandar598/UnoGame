package bg.sofia.uni.fmi.mjt.player;

import bg.sofia.uni.fmi.mjt.game.card.Card;
import bg.sofia.uni.fmi.mjt.exception.CardNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnoPlayer implements Player {

    private List<Card> hand;
    private String name;
    private boolean hasSaidUno;
    private boolean hasWon;
    private int id;
    private PlayerStatus status;
    private static final int UNO_CARD_COUNT = 1;

    public UnoPlayer(String name, int id) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.name = name;
        this.hasSaidUno = false;
        this.hand = new ArrayList<>();
        this.id = id;
        this.hasWon = false;
        this.status = PlayerStatus.NOT_IN_GAME;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("card cannot be null");
        }
        hand.add(card);
    }

    @Override
    public List<Card> getCards() {
        return Collections.unmodifiableList(hand);
    }

    @Override
    public Card playCard(int cardId) throws CardNotFoundException {

        for (Card c : this.hand) {
            if (c.getCardID() == cardId) {
                hand.remove(c);
                return c;
            }
        }
        throw new CardNotFoundException("hand does not contain such element");
    }

    @Override
    public boolean hasUno() {
        return this.hand.size() == 1;
    }

    @Override
    public void sayUno() throws UnoUserException {
        if (hand.size() > UNO_CARD_COUNT) {
            throw new UnoUserException("Cannot say UNO with 2 or more cards");
        }
        this.hasSaidUno = true;
    }

    @Override
    public boolean hasSaidUno() {
        return this.hasSaidUno;
    }

    @Override
    public void resetUnoStatus() {
        this.hasSaidUno = false;
    }

    @Override
    public boolean hasWon() {
        return this.hasWon;
    }

    @Override
    public void setHasWon(boolean hasWon) {
        this.hasWon = hasWon;
    }

    @Override
    public void setPlayerStatus(PlayerStatus status) {
        this.status = status;
    }

    @Override
    public PlayerStatus getPlayerStatus() {
        return this.status;
    }

    @Override
    public int getId() {
        return this.id;
    }
}
