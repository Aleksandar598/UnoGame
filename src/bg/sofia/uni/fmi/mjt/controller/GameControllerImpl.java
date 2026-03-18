package bg.sofia.uni.fmi.mjt.controller;

import bg.sofia.uni.fmi.mjt.card.Card;
import bg.sofia.uni.fmi.mjt.card.CardColour;
import bg.sofia.uni.fmi.mjt.card.EffectCard;
import bg.sofia.uni.fmi.mjt.deck.Deck;
import bg.sofia.uni.fmi.mjt.exception.CannotStartGameException;
import bg.sofia.uni.fmi.mjt.exception.CardNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.GameHasStartedException;
import bg.sofia.uni.fmi.mjt.exception.MaximumPlayerCountReached;
import bg.sofia.uni.fmi.mjt.exception.NoColourSelectedException;
import bg.sofia.uni.fmi.mjt.exception.NotAColourChangeCardException;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.logger.GameLogger;
import bg.sofia.uni.fmi.mjt.logger.Logger;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.player.PlayerStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameControllerImpl implements GameController {
    private List<Player> playerList;
    private Deck deck;
    private boolean skipNextPlayer = false;
    private int cardCountForDraw = 0;
    private int currentPlayerIndex = 0;
    private boolean clockWise = true;
    private CardColour currentColour;
    private boolean hasStarted = false;
    private Logger logger;
    private final int gameCreator;

    private static final int MIN_AMOUNT_OF_PLAYERS = 2;
    private static final int MAX_AMOUNT_OF_PLAYERS = 8;
    private static final int STARTING_CARD_COUNT = 7;

    public GameControllerImpl(Deck deck, Player creator) {
        if (deck == null) {
            throw new IllegalArgumentException("deck cannot be null");
        }
        this.deck = deck;
        currentColour = deck.getLastPlayedCard().getCardColour();
        playerList = new ArrayList<>();
        this.logger = new GameLogger();
        this.gameCreator = creator.getId();
    }

    @Override
    public void skipNextPlayer() {
        skipNextPlayer = true;
    }

    @Override
    public void removePlayer(int playerId) throws PlayerNotFoundException {
        Player playerToRemove = getPlayer(playerId);
        int indexToRemove = playerList.indexOf(playerToRemove);

        playerList.remove(indexToRemove);

        if (indexToRemove < currentPlayerIndex) {
            currentPlayerIndex--;
        }
        if (currentPlayerIndex >= playerList.size()) {
            currentPlayerIndex = 0;
        }
    }

    @Override
    public void addCardForDraw(int count) {
        cardCountForDraw += count;
    }

    @Override
    public void reversePlayerDirection() {
        clockWise = !clockWise;
    }

    @Override
    public void setColour(CardColour colour) {
        if (colour == null) {
            throw new IllegalArgumentException("colour cannot be null");
        }
        this.currentColour = colour;
    }

    @Override
    public Player getPlayer(int playerId) throws PlayerNotFoundException {
        for (Player p : playerList) {
            if (p.getId() == playerId) {
                return p;
            }
        }
        throw new PlayerNotFoundException("Player not found in game");
    }

    @Override
    public Player getCurrentPlayer() {
        return this.playerList.get(currentPlayerIndex);
    }

    @Override
    public void nextTurn() {

        moveIndex();
        skipNonPlayingPlayers();
        if (skipNextPlayer) {
            skipNextPlayer = false;
            moveIndex();
        }
        skipNonPlayingPlayers();
    }

    private void moveIndex() {
        if (clockWise) {
            currentPlayerIndex = (currentPlayerIndex + 1) % playerList.size();
        } else currentPlayerIndex--;

        if (currentPlayerIndex < 0) currentPlayerIndex = playerList.size() - 1;

    }

    private void skipNonPlayingPlayers() {
        while (playerList.get(currentPlayerIndex).getPlayerStatus() != PlayerStatus.PLAYING) {
            moveIndex();
        }
    }

    @Override
    public Card getTopCard() {
        return deck.getLastPlayedCard();
    }

    @Override
    public CardColour getCurrentColour() {
        return this.currentColour;
    }

    @Override
    public int penaltyCardCount() {
        return this.cardCountForDraw;
    }

    @Override
    public boolean isTherePendingCardDraw() {
        return cardCountForDraw != 0;
    }

    @Override
    public Card drawCard(int playerId) throws PlayerNotFoundException {
        Player p = getPlayer(playerId);
        Card c = deck.drawCard();
        p.addCard(c);
        return c;
    }

    @Override
    public String acceptPenalty(int playerId) throws PlayerNotFoundException {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < cardCountForDraw; i++) {
            builder.append(i)
                    .append(". ")
                    .append(drawCard(playerId).getCardAsString())
                    .append(System.lineSeparator());
        }
        resetPenalty();
        return builder.toString();
    }

    private void resetPenalty() {
        this.cardCountForDraw = 0;
    }

    @Override
    public void playCard(int playerId, int cardId) throws CardNotFoundException, PlayerNotFoundException, NoColourSelectedException {
        Card c = findCard(playerId, cardId);

        if (c.getCardColour() == CardColour.SPECIAL) {
            throw new NoColourSelectedException("Card is a joker but no colour has been selected");
        }
        deck.playCard(c);
        applyCardEffect(c);
        logger.logCard(c);
        getPlayer(playerId).getCards().remove(c);
        checkForWinner(playerId);
    }

    @Override
    public void playCard(int playerId, int cardId, CardColour colour) throws CardNotFoundException, PlayerNotFoundException, NotAColourChangeCardException, NoColourSelectedException {
        Card c = findCard(playerId, cardId);

        if (c.getCardColour() != CardColour.SPECIAL) {
            throw new NotAColourChangeCardException("Card is not a colour change card");
        }
        deck.playCard(c);
        currentColour = colour;
        getPlayer(playerId).getCards().remove(c);
        logger.logCard(c);
        checkForWinner(playerId);
        applyCardEffect(c);
    }

    private void applyCardEffect(Card c) throws NoColourSelectedException {
        if (c instanceof EffectCard) {
            ((EffectCard) c).applyEffect(this);
        }
    }

    private void checkForWinner(int playerId) throws PlayerNotFoundException {
        Player player = getPlayer(playerId);
        if (player.getCards().isEmpty()) {
            player.setHasWon(true);
            player.setPlayerStatus(PlayerStatus.HAS_WON);
            logger.logWinner(player);
        }
    }

    private Card findCard(int playerId, int cardId) throws CardNotFoundException, PlayerNotFoundException {
        Card searched = null;
        Player p = getPlayer(playerId);
        for (Card c : p.getCards()) {
            if (c.getCardID() == cardId) {
                searched = c;
                return c;
            }
        }

        throw new CardNotFoundException("card has not been found");
    }

    @Override
    public boolean checkPlayerCanPlayAnyCards(int playerId) throws PlayerNotFoundException {
        Player p = getPlayer(playerId);

        boolean canPlay = false;

        for (Card c : p.getCards()) {
            if (c.isCardPlayable(deck.getLastPlayedCard(), currentColour)) {
                canPlay = true;
            }
        }

        return canPlay;
    }

    @Override
    public void addPlayer(Player player) throws GameHasStartedException, MaximumPlayerCountReached {

        if (hasStarted) {
            throw new GameHasStartedException("Cannot join game in progress");
        }
        if (playerList.size() >= MAX_AMOUNT_OF_PLAYERS) {
            throw new MaximumPlayerCountReached("Maximum player count reached");
        }
        this.playerList.add(player);
        player.setPlayerStatus(PlayerStatus.WAITING_TO_START);
    }

    @Override
    public String getPlayedCardsLog() {
        return logger.getCardLog();
    }

    @Override
    public String getWinnerLog() {
        return logger.getWinnerLog();
    }

    @Override
    public Player getCreator() throws PlayerNotFoundException {
        return getPlayer(gameCreator);
    }

    public boolean hasStarted() {
        return this.hasStarted;
    }

    @Override
    public void startGame() throws CannotStartGameException {
        if (playerList.size() < MIN_AMOUNT_OF_PLAYERS) {
            throw new CannotStartGameException("Cannot start game without at least 2 players");
        }
        giveStartingCards();
        this.hasStarted = true;

        if (deck.getLastPlayedCard().getCardColour() == CardColour.SPECIAL) {
            List<CardColour> colours = new ArrayList<>(List.of(CardColour.values()));
            colours.remove(CardColour.SPECIAL);
            Random rand = new Random();
            this.currentColour = colours.get(rand.nextInt(colours.size()));
        }
    }

    private void giveStartingCards() {
        for (Player p : playerList) {
            for (int i = 0; i < STARTING_CARD_COUNT; i++) {
                p.addCard(deck.drawCard());
            }
            p.setPlayerStatus(PlayerStatus.PLAYING);
        }
    }

}
