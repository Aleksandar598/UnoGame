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
import bg.sofia.uni.fmi.mjt.game.card.ChooseColourCard;
import bg.sofia.uni.fmi.mjt.game.card.EffectCard;
import bg.sofia.uni.fmi.mjt.game.deck.Deck;
import bg.sofia.uni.fmi.mjt.logger.GameLogger;
import bg.sofia.uni.fmi.mjt.logger.Logger;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.player.PlayerStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameControllerImpl implements GameController {
    private final List<Player> playerList;
    private final Deck deck;
    private boolean skipNextPlayer = false;
    private int cardCountForDraw = 0;
    private int currentPlayerIndex = 0;
    private boolean clockWise = true;
    private CardColour currentColour;
    private boolean hasStarted = false;
    private final Logger logger;
    private int maxPlayers;

    private static final int MIN_AMOUNT_OF_PLAYERS = 2;
    private static final List<CardColour> PLAYABLE_COLOURS = List.of(CardColour.GREEN,
            CardColour.BLUE, CardColour.YELLOW, CardColour.RED);
    public static final int MAX_PLAYER_COUNT = 8;
    private static final int STARTING_CARD_COUNT = 7;

    public GameControllerImpl(Deck deck, int maxPlayers) {
        if (deck == null) {
            throw new IllegalArgumentException("deck cannot be null");
        }
        this.deck = deck;
        currentColour = deck.getLastPlayedCard().getCardColour();
        playerList = new ArrayList<>();
        this.logger = new GameLogger();
        this.maxPlayers = maxPlayers;
    }

    public GameControllerImpl(Deck deck) {
        if (deck == null) {
            throw new IllegalArgumentException("deck cannot be null");
        }
        this.deck = deck;
        currentColour = deck.getLastPlayedCard().getCardColour();
        playerList = new ArrayList<>();
        this.logger = new GameLogger();
        this.maxPlayers = MAX_PLAYER_COUNT;
    }

    @Override
    public void skipNextPlayer() {
        skipNextPlayer = true;
    }

    @Override
    public void removePlayer(int playerId) throws PlayerNotFoundException {
        Player playerToRemove = getPlayer(playerId);
        deck.returnCards(playerToRemove.getCards());
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
        if (count <= 0) {
            throw new IllegalArgumentException("count must be greater than 0");
        }
        cardCountForDraw += count;
    }

    @Override
    public void reversePlayerDirection() {
        clockWise = !clockWise;
        if (playingPlayerCount() == 2) {
            skipNextPlayer();
        }
    }

    @Override
    public void setColour(CardColour colour) throws NoColourSelectedException {
        if (colour == null) {
            throw new IllegalArgumentException("colour cannot be null");
        }
        if (!PLAYABLE_COLOURS.contains(colour)) {
            throw new NoColourSelectedException("Colour not selected");
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
        if (playingPlayerCount()  < 1) {
            return;
        }

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
    public Card drawCard(int playerId) throws PlayerNotFoundException, UnoUserException,
            GameNotStartedException, InvalidActionException {
        if (!hasStarted) {
            throw new GameNotStartedException("Game not started");
        }
        requireCurrentPlayer(playerId);
        if (isTherePendingCardDraw()) {
            throw new InvalidActionException("You must accept the pending effect");
        }
        if (checkPlayerCanPlayAnyCards(playerId)) {
            throw new InvalidActionException("You have a card you can play");
        }

        Card card = drawOneCard(playerId);
        nextTurn();
        return card;
    }

    @Override
    public String acceptPenalty(int playerId) throws PlayerNotFoundException, UnoUserException {
        StringBuilder builder = new StringBuilder();

        requireCurrentPlayer(playerId);

        if (!isTherePendingCardDraw()) {
            throw new UnoUserException("There is no pending effect");
        }
        for (int i = 0; i < cardCountForDraw; i++) {
            builder.append(i)
                    .append(". ")
                    .append(drawOneCard(playerId).getCardAsString())
                    .append(System.lineSeparator());
        }
        resetPenalty();
        nextTurn();
        return builder.toString();
    }

    private void resetPenalty() {
        this.cardCountForDraw = 0;
    }

    @Override
    public String playCard(int playerId, int cardId) throws CardNotFoundException, PlayerNotFoundException,
            NoColourSelectedException, UnoUserException, CannotPlayCardException {
        requireCurrentPlayer(playerId);
        Card c = findCard(playerId, cardId);
        Player p = getPlayer(playerId);

        if (isTherePendingCardDraw()) {
            throw new CannotPlayCardException("You must accept the pending effect");
        }

        if (c.getCardColour() == CardColour.SPECIAL) {
            throw new NoColourSelectedException("Card is a joker but no colour has been selected");
        }

        if (!c.isCardPlayable(deck.getLastPlayedCard(), this.currentColour)) {
            throw new CannotPlayCardException("Card Cannot be Played");
        }

        deck.playCard(c);
        applyCardEffect(c);
        logger.logCard(c, p);
        getPlayer(playerId).playCard(cardId);
        checkForWinner(playerId);
        return c.getCardAsString();
    }

    @Override
    public String playCard(int playerId, int cardId, CardColour colour) throws CardNotFoundException,
            PlayerNotFoundException, NoColourSelectedException, UnoUserException,
            CannotPlayCardException, GameNotStartedException, NotAColourChangeCardException {
        requireCurrentPlayer(playerId);
        Card c = findCard(playerId, cardId);
        Player p = getPlayer(playerId);
        if (!hasStarted) {
            throw new GameNotStartedException("Game has not started");
        }
        if (isTherePendingCardDraw()) {
            throw new CannotPlayCardException("You must accept the pending effect");
        }
        if (c.getCardColour() != CardColour.SPECIAL) {
            throw new NotAColourChangeCardException("Card is not a colour change card");
        }
        if (!c.isCardPlayable(deck.getLastPlayedCard(), this.currentColour)) {
            throw new CannotPlayCardException("Card Cannot be Played");
        }
        if (!PLAYABLE_COLOURS.contains(colour)) {
            throw new NoColourSelectedException("Colour is not a playable colour");
        }
        deck.playCard(c);
        currentColour = colour;
        if (c instanceof ChooseColourCard) {
            ((ChooseColourCard) c).setChosenColour(colour);
        }
        getPlayer(playerId).playCard(cardId);
        logger.logCard(c, p);
        checkForWinner(playerId);
        applyCardEffect(c);
        return c.getCardAsString();
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
        if (player == null) {
            throw new IllegalArgumentException("player cannot be null");
        }
        if (playerList.stream().anyMatch(p -> p.getId() == player.getId())) {
            throw new IllegalArgumentException("Player is already in the game");
        }

        if (hasStarted) {
            throw new GameHasStartedException("Cannot join game in progress");
        }
        if (playerList.size() >= maxPlayers) {
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
    public boolean hasStarted() {
        return this.hasStarted;
    }

    @Override
    public boolean hasEnded() {
        return hasStarted && playingPlayerCount() <= 1;
    }

    @Override
    public void startGame() throws CannotStartGameException {
        if (playerList.size() < MIN_AMOUNT_OF_PLAYERS) {
            throw new CannotStartGameException("Cannot start game without at least 2 players");
        }
        giveStartingCards();
        this.hasStarted = true;

        if (deck.getLastPlayedCard().getCardColour() == CardColour.SPECIAL) {
            List<CardColour> colours = PLAYABLE_COLOURS;
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

    private void requireCurrentPlayer(int playerId) throws UnoUserException {
        if (playerId != getCurrentPlayer().getId()) {
            throw new UnoUserException("It is not your turn");
        }
    }

    private Card drawOneCard(int playerId) throws PlayerNotFoundException {
        Player player = getPlayer(playerId);
        Card card = deck.drawCard();
        player.addCard(card);
        return card;
    }

    private long playingPlayerCount() {
        return playerList.stream().filter(player -> player.getPlayerStatus() == PlayerStatus.PLAYING).count();
    }

}
