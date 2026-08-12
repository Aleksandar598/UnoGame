package bg.sofia.uni.fmi.mjt.controller;

import bg.sofia.uni.fmi.mjt.card.Card;
import bg.sofia.uni.fmi.mjt.card.CardColour;
import bg.sofia.uni.fmi.mjt.card.CardType;
import bg.sofia.uni.fmi.mjt.deck.Deck;
import bg.sofia.uni.fmi.mjt.exception.*;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.player.PlayerStatus;
import bg.sofia.uni.fmi.mjt.player.UnoPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameControllerImplTest {
    private static final Card TOP_CARD = Card.of(CardColour.RED, CardType.FIVE, 1);
    private static final Card FIRST_DRAWN_CARD = Card.of(CardColour.BLUE, CardType.ONE, 2);
    private static final Card SECOND_DRAWN_CARD = Card.of(CardColour.GREEN, CardType.TWO, 3);
    private static final Card THIRD_DRAWN_CARD = Card.of(CardColour.YELLOW, CardType.THREE, 4);

    private StubDeck deck;
    private UnoPlayer creator;
    private GameControllerImpl controller;

    @BeforeEach
    void setUp() {
        deck = new StubDeck(TOP_CARD, FIRST_DRAWN_CARD, SECOND_DRAWN_CARD, THIRD_DRAWN_CARD);
        creator = new UnoPlayer("creator", 1);
        controller = new GameControllerImpl(deck, creator);
    }

    @Test
    void testConstructorRejectsNullDeck() {
        assertThrows(IllegalArgumentException.class, () -> new GameControllerImpl(null, creator));
    }

    @Test
    void testConstructorUsesTopCardColour() {
        assertEquals(CardColour.RED, controller.getCurrentColour());
        assertEquals(TOP_CARD, controller.getTopCard());
    }

    @Test
    void testAddPlayerMakesPlayerWaitForTheGameToStart() throws Exception {
        controller.addPlayer(creator);

        assertEquals(creator, controller.getPlayer(creator.getId()));
        assertEquals(PlayerStatus.WAITING_TO_START, creator.getPlayerStatus());
        assertEquals(creator, controller.getCreator());
    }

    @Test
    void testStartGameRequiresAtLeastTwoPlayers() throws Exception {
        controller.addPlayer(creator);

        assertThrows(CannotStartGameException.class, controller::startGame);
        assertFalse(controller.hasStarted());
    }

    @Test
    void testStartGameDealsSevenCardsAndMarksPlayersAsPlaying() throws Exception {
        deck.addDrawCards(20);
        UnoPlayer secondPlayer = new UnoPlayer("second", 2);
        controller.addPlayer(creator);
        controller.addPlayer(secondPlayer);

        controller.startGame();

        assertTrue(controller.hasStarted());
        assertEquals(7, creator.getCards().size());
        assertEquals(7, secondPlayer.getCards().size());
        assertEquals(PlayerStatus.PLAYING, creator.getPlayerStatus());
        assertEquals(PlayerStatus.PLAYING, secondPlayer.getPlayerStatus());
    }

    @Test
    void testAddPlayerRejectsNinthPlayer() throws Exception {
        for (int id = 1; id <= 8; id++) {
            controller.addPlayer(new UnoPlayer("player" + id, id));
        }

        assertThrows(MaximumPlayerCountReached.class,
                () -> controller.addPlayer(new UnoPlayer("player9", 9)));
    }

    @Test
    void testDrawCardAddsTheDrawnCardToThePlayerHand() throws Exception {
        controller.addPlayer(creator);

        Card drawnCard = controller.drawCard(creator.getId());

        assertEquals(FIRST_DRAWN_CARD, drawnCard);
        assertEquals(1, creator.getCards().size());
        assertEquals(FIRST_DRAWN_CARD, creator.getCards().get(0));
    }

    @Test
    void testAcceptsPenaltyDrawsAllPendingCardsAndResetsThePenalty() throws Exception {
        controller.addPlayer(creator);
        controller.addCardForDraw(2);

        String result = controller.acceptPenalty(creator.getId());

        assertFalse(controller.isTherePendingCardDraw());
        assertEquals(0, controller.penaltyCardCount());
        assertEquals(2, creator.getCards().size());
        assertTrue(result.contains(FIRST_DRAWN_CARD.getCardAsString()));
        assertTrue(result.contains(SECOND_DRAWN_CARD.getCardAsString()));
    }

    @Test
    void testNextTurnMovesClockwiseByDefault() throws Exception {
        UnoPlayer secondPlayer = new UnoPlayer("second", 2);
        UnoPlayer thirdPlayer = new UnoPlayer("third", 3);
        addPlayingPlayers(creator, secondPlayer, thirdPlayer);

        controller.nextTurn();

        assertEquals(secondPlayer, controller.getCurrentPlayer());
    }

    @Test
    void testNextTurnUsesTheReversedDirection() throws Exception {
        UnoPlayer secondPlayer = new UnoPlayer("second", 2);
        UnoPlayer thirdPlayer = new UnoPlayer("third", 3);
        addPlayingPlayers(creator, secondPlayer, thirdPlayer);
        controller.reversePlayerDirection();

        controller.nextTurn();

        assertEquals(thirdPlayer, controller.getCurrentPlayer());
    }

    @Test
    void testSkipNextPlayerSkipsExactlyOnePlayingPlayer() throws Exception {
        UnoPlayer secondPlayer = new UnoPlayer("second", 2);
        UnoPlayer thirdPlayer = new UnoPlayer("third", 3);
        addPlayingPlayers(creator, secondPlayer, thirdPlayer);
        controller.skipNextPlayer();

        controller.nextTurn();

        assertEquals(thirdPlayer, controller.getCurrentPlayer());
    }

    @Test
    void testCheckPlayerCanPlayAnyCardsUsesTheTopCardAndCurrentColour() throws Exception {
        controller.addPlayer(creator);
        creator.addCard(Card.of(CardColour.BLUE, CardType.ONE, 10));
        assertFalse(controller.checkPlayerCanPlayAnyCards(creator.getId()));

        creator.addCard(Card.of(CardColour.GREEN, CardType.FIVE, 11));
        assertTrue(controller.checkPlayerCanPlayAnyCards(creator.getId()));
    }

    @Test
    void testRemovePlayerMakesThePlayerUnavailable() throws Exception {
        controller.addPlayer(creator);
        controller.removePlayer(creator.getId());

        assertThrows(PlayerNotFoundException.class, () -> controller.getPlayer(creator.getId()));
    }

    @Test
    void testAddPlayerRejectsJoiningAfterTheGameHasStarted() throws Exception {
        deck.addDrawCards(20);
        UnoPlayer secondPlayer = new UnoPlayer("second", 2);
        controller.addPlayer(creator);
        controller.addPlayer(secondPlayer);
        controller.startGame();

        assertThrows(GameHasStartedException.class,
                () -> controller.addPlayer(new UnoPlayer("late", 3)));
    }

    @Test
    void testNormalPlayPlacesTheCardOnThePileRemovesItFromTheHandAndLogsTheWinner() throws Exception {
        MutablePlayer secondPlayer = new MutablePlayer("second", 2);
        Card matchingCard = Card.of(CardColour.RED, CardType.ONE, 20);
        creator.addCard(matchingCard);
        addPlayingPlayers(controller, creator, secondPlayer);

        controller.playCard(creator.getId(), matchingCard.getCardID());

        assertEquals(matchingCard, deck.getLastPlayedCard());
        assertTrue(creator.getCards().isEmpty());
        assertTrue(controller.getWinnerLog().contains(creator.getName()));
    }

    @Test
    void testNormalPlayRejectsCardThatDoesNotMatchColourOrType() throws Exception {
        MutablePlayer firstPlayer = new MutablePlayer("first", 1);
        GameControllerImpl localController = new GameControllerImpl(deck, firstPlayer);
        Card nonMatchingCard = Card.of(CardColour.BLUE, CardType.ONE, 21);
        firstPlayer.addCard(nonMatchingCard);
        MutablePlayer secondPlayer = new MutablePlayer("second", 2);
        addPlayingPlayers(localController, firstPlayer, secondPlayer);

        assertThrows(CannotPlayCardException.class,
                () -> localController.playCard(firstPlayer.getId(), nonMatchingCard.getCardID()));
        assertEquals(TOP_CARD, localController.getTopCard());
        assertEquals(1, firstPlayer.getCards().size());
    }

    @Test
    void testNormalPlayRejectsAColourChangingCardWithoutAChosenColour() throws Exception {
        Card wildCard = Card.of(CardColour.SPECIAL, CardType.CHOOSE_COLOUR_CARD, 22);
        creator.addCard(wildCard);
        UnoPlayer secondPlayer = new UnoPlayer("second", 2);
        addPlayingPlayers(creator, secondPlayer);

        assertThrows(NoColourSelectedException.class,
                () -> controller.playCard(creator.getId(), wildCard.getCardID()));
    }

    @Test
    void testSpecialPlayRejectsANonWildCard() throws Exception {
        Card normalCard = Card.of(CardColour.RED, CardType.ONE, 23);
        creator.addCard(normalCard);
        UnoPlayer secondPlayer = new UnoPlayer("second", 2);
        addPlayingPlayers(creator, secondPlayer);

        assertThrows(NotAColourChangeCardException.class,
                () -> controller.playCard(creator.getId(), normalCard.getCardID(), CardColour.BLUE));
    }

    @Test
    void testPlusTwoAddsAPenaltyForTheNextPlayer() throws Exception {
        MutablePlayer firstPlayer = new MutablePlayer("first", 1);
        GameControllerImpl localController = new GameControllerImpl(deck, firstPlayer);
        MutablePlayer secondPlayer = new MutablePlayer("second", 2);
        Card plusTwo = Card.of(CardColour.RED, CardType.PLUS_TWO_CARD, 24);
        firstPlayer.addCard(plusTwo);
        addPlayingPlayers(localController, firstPlayer, secondPlayer);

        localController.playCard(firstPlayer.getId(), plusTwo.getCardID());

        assertTrue(localController.isTherePendingCardDraw());
        assertEquals(2, localController.penaltyCardCount());
        assertEquals(plusTwo, localController.getTopCard());
    }

    @Test
    void testAcceptingAPlusTwoPenaltyDrawsTwoCardsAndClearsThePenalty() throws Exception {
        MutablePlayer firstPlayer = new MutablePlayer("first", 1);
        GameControllerImpl localController = new GameControllerImpl(deck, firstPlayer);
        MutablePlayer secondPlayer = new MutablePlayer("second", 2);
        Card plusTwo = Card.of(CardColour.RED, CardType.PLUS_TWO_CARD, 27);
        firstPlayer.addCard(plusTwo);
        addPlayingPlayers(localController, firstPlayer, secondPlayer);

        localController.playCard(firstPlayer.getId(), plusTwo.getCardID());
        localController.acceptPenalty(secondPlayer.getId());

        assertEquals(2, secondPlayer.getCards().size());
        assertFalse(localController.isTherePendingCardDraw());
        assertEquals(0, localController.penaltyCardCount());
    }

    @Test
    void testPlayerWithAPendingPlusTwoPenaltyCannotPlayANormalCard() throws Exception {
        MutablePlayer firstPlayer = new MutablePlayer("first", 1);
        GameControllerImpl localController = new GameControllerImpl(deck, firstPlayer);
        MutablePlayer secondPlayer = new MutablePlayer("second", 2);
        Card plusTwo = Card.of(CardColour.RED, CardType.PLUS_TWO_CARD, 30);
        Card normalCard = Card.of(CardColour.RED, CardType.ONE, 31);
        firstPlayer.addCard(plusTwo);
        secondPlayer.addCard(normalCard);
        addPlayingPlayers(localController, firstPlayer, secondPlayer);

        localController.playCard(firstPlayer.getId(), plusTwo.getCardID());
        localController.nextTurn();

        assertThrows(CannotPlayCardException.class,
                () -> localController.playCard(secondPlayer.getId(), normalCard.getCardID()));
        assertEquals(plusTwo, localController.getTopCard());
        assertEquals(1, secondPlayer.getCards().size());
    }

    @Test
    void testPlayerWithAPendingPlusTwoPenaltyCannotRespondWithPlusFour() throws Exception {
        MutablePlayer firstPlayer = new MutablePlayer("first", 1);
        GameControllerImpl localController = new GameControllerImpl(deck, firstPlayer);
        MutablePlayer secondPlayer = new MutablePlayer("second", 2);
        Card plusTwo = Card.of(CardColour.RED, CardType.PLUS_TWO_CARD, 32);
        Card plusFour = Card.of(CardColour.SPECIAL, CardType.PLUS_FOUR_CARD, 33);
        firstPlayer.addCard(plusTwo);
        secondPlayer.addCard(plusFour);
        addPlayingPlayers(localController, firstPlayer, secondPlayer);

        localController.playCard(firstPlayer.getId(), plusTwo.getCardID());
        localController.nextTurn();

        assertThrows(CannotPlayCardException.class,
                () -> localController.playCard(secondPlayer.getId(), plusFour.getCardID(), CardColour.BLUE));
        assertEquals(plusTwo, localController.getTopCard());
        assertEquals(1, secondPlayer.getCards().size());
    }

    @Test
    void testPlusFourChangesColourAndAddsAFourCardPenalty() throws Exception {
        MutablePlayer firstPlayer = new MutablePlayer("first", 1);
        GameControllerImpl localController = new GameControllerImpl(deck, firstPlayer);
        MutablePlayer secondPlayer = new MutablePlayer("second", 2);
        Card plusFour = Card.of(CardColour.SPECIAL, CardType.PLUS_FOUR_CARD, 28);
        firstPlayer.addCard(plusFour);
        addPlayingPlayers(localController, firstPlayer, secondPlayer);

        localController.playCard(firstPlayer.getId(), plusFour.getCardID(), CardColour.GREEN);

        assertEquals(plusFour, localController.getTopCard());
        assertEquals(CardColour.GREEN, localController.getCurrentColour());
        assertTrue(localController.isTherePendingCardDraw());
        assertEquals(4, localController.penaltyCardCount());
    }

    @Test
    void testAcceptingAPlusFourPenaltyDrawsFourCardsAndClearsThePenalty() throws Exception {
        deck.addDrawCards(4);
        MutablePlayer firstPlayer = new MutablePlayer("first", 1);
        GameControllerImpl localController = new GameControllerImpl(deck, firstPlayer);
        MutablePlayer secondPlayer = new MutablePlayer("second", 2);
        Card plusFour = Card.of(CardColour.SPECIAL, CardType.PLUS_FOUR_CARD, 29);
        firstPlayer.addCard(plusFour);
        addPlayingPlayers(localController, firstPlayer, secondPlayer);

        localController.playCard(firstPlayer.getId(), plusFour.getCardID(), CardColour.BLUE);
        localController.acceptPenalty(secondPlayer.getId());

        assertEquals(4, secondPlayer.getCards().size());
        assertFalse(localController.isTherePendingCardDraw());
        assertEquals(0, localController.penaltyCardCount());
    }

    @Test
    void testChooseColourCardSetsTheSelectedColourWithoutAddingAPenalty() throws Exception {
        MutablePlayer firstPlayer = new MutablePlayer("first", 1);
        GameControllerImpl localController = new GameControllerImpl(deck, firstPlayer);
        MutablePlayer secondPlayer = new MutablePlayer("second", 2);
        Card chooseColour = Card.of(CardColour.SPECIAL, CardType.CHOOSE_COLOUR_CARD, 34);
        firstPlayer.addCard(chooseColour);
        firstPlayer.addCard(Card.of(CardColour.RED, CardType.ONE, 35));
        addPlayingPlayers(localController, firstPlayer, secondPlayer);

        localController.playCard(firstPlayer.getId(), chooseColour.getCardID(), CardColour.YELLOW);

        assertEquals(chooseColour, localController.getTopCard());
        assertEquals(CardColour.YELLOW, localController.getCurrentColour());
        assertFalse(localController.isTherePendingCardDraw());
    }

    @Test
    void testSpecialPlayRejectsNonPlayableSelectedColours() throws Exception {
        MutablePlayer firstPlayer = new MutablePlayer("first", 1);
        GameControllerImpl localController = new GameControllerImpl(deck, firstPlayer);
        MutablePlayer secondPlayer = new MutablePlayer("second", 2);
        Card chooseColour = Card.of(CardColour.SPECIAL, CardType.CHOOSE_COLOUR_CARD, 36);
        firstPlayer.addCard(chooseColour);
        addPlayingPlayers(localController, firstPlayer, secondPlayer);

        assertThrows(IllegalArgumentException.class,
                () -> localController.playCard(firstPlayer.getId(), chooseColour.getCardID(), CardColour.SPECIAL));
    }

    @Test
    void testSkipCardWithTwoPlayersGivesTheTurnBackToThePlayerWhoPlayedIt() throws Exception {
        MutablePlayer firstPlayer = new MutablePlayer("first", 1);
        GameControllerImpl localController = new GameControllerImpl(deck, firstPlayer);
        MutablePlayer secondPlayer = new MutablePlayer("second", 2);
        Card skipCard = Card.of(CardColour.RED, CardType.SKIP_MOVE_CARD, 37);
        firstPlayer.addCard(skipCard);
        firstPlayer.addCard(Card.of(CardColour.RED, CardType.ONE, 38));
        addPlayingPlayers(localController, firstPlayer, secondPlayer);

        localController.playCard(firstPlayer.getId(), skipCard.getCardID());
        localController.nextTurn();

        assertEquals(firstPlayer, localController.getCurrentPlayer());
    }

    @Test
    void testReverseCardWithTwoPlayersGivesTheTurnBackToThePlayerWhoPlayedIt() throws Exception {
        MutablePlayer firstPlayer = new MutablePlayer("first", 1);
        GameControllerImpl localController = new GameControllerImpl(deck, firstPlayer);
        MutablePlayer secondPlayer = new MutablePlayer("second", 2);
        Card reverseCard = Card.of(CardColour.RED, CardType.SWITCH_CARD, 39);
        firstPlayer.addCard(reverseCard);
        firstPlayer.addCard(Card.of(CardColour.RED, CardType.ONE, 40));
        addPlayingPlayers(localController, firstPlayer, secondPlayer);

        localController.playCard(firstPlayer.getId(), reverseCard.getCardID());
        localController.nextTurn();

        assertEquals(firstPlayer, localController.getCurrentPlayer());
    }

    @Test
    void testWinningPlayerIsSkippedOnLaterTurns() throws Exception {
        MutablePlayer firstPlayer = new MutablePlayer("first", 1);
        GameControllerImpl localController = new GameControllerImpl(deck, firstPlayer);
        MutablePlayer secondPlayer = new MutablePlayer("second", 2);
        MutablePlayer thirdPlayer = new MutablePlayer("third", 3);
        Card lastCard = Card.of(CardColour.RED, CardType.ONE, 41);
        firstPlayer.addCard(lastCard);
        addPlayingPlayers(localController, firstPlayer, secondPlayer, thirdPlayer);

        localController.playCard(firstPlayer.getId(), lastCard.getCardID());
        localController.nextTurn();

        assertTrue(firstPlayer.hasWon());
        assertEquals(PlayerStatus.HAS_WON, firstPlayer.getPlayerStatus());
        assertEquals(secondPlayer, localController.getCurrentPlayer());
    }

    @Test
    void testRemovingTheCurrentPlayerSelectsTheFollowingPlayerAsCurrent() throws Exception {
        MutablePlayer firstPlayer = new MutablePlayer("first", 1);
        GameControllerImpl localController = new GameControllerImpl(deck, firstPlayer);
        MutablePlayer secondPlayer = new MutablePlayer("second", 2);
        MutablePlayer thirdPlayer = new MutablePlayer("third", 3);
        addPlayingPlayers(localController, firstPlayer, secondPlayer, thirdPlayer);

        localController.removePlayer(firstPlayer.getId());

        assertEquals(secondPlayer, localController.getCurrentPlayer());
    }

    @Test
    void testDrawCardRejectsAnUnknownPlayer() {
        assertThrows(PlayerNotFoundException.class, () -> controller.drawCard(99));
    }

    @Test
    void testSkipCardSkipsTheNextPlayer() throws Exception {
        MutablePlayer firstPlayer = new MutablePlayer("first", 1);
        GameControllerImpl localController = new GameControllerImpl(deck, firstPlayer);
        MutablePlayer secondPlayer = new MutablePlayer("second", 2);
        MutablePlayer thirdPlayer = new MutablePlayer("third", 3);
        Card skipCard = Card.of(CardColour.RED, CardType.SKIP_MOVE_CARD, 25);
        firstPlayer.addCard(skipCard);
        addPlayingPlayers(localController, firstPlayer, secondPlayer, thirdPlayer);

        localController.playCard(firstPlayer.getId(), skipCard.getCardID());
        localController.nextTurn();

        assertEquals(thirdPlayer, localController.getCurrentPlayer());
    }

    @Test
    void testReverseCardChangesTheDirectionOfTheFollowingTurn() throws Exception {
        MutablePlayer firstPlayer = new MutablePlayer("first", 1);
        GameControllerImpl localController = new GameControllerImpl(deck, firstPlayer);
        MutablePlayer secondPlayer = new MutablePlayer("second", 2);
        MutablePlayer thirdPlayer = new MutablePlayer("third", 3);
        Card reverseCard = Card.of(CardColour.RED, CardType.SWITCH_CARD, 26);
        firstPlayer.addCard(reverseCard);
        addPlayingPlayers(localController, firstPlayer, secondPlayer, thirdPlayer);

        localController.playCard(firstPlayer.getId(), reverseCard.getCardID());
        localController.nextTurn();

        assertEquals(thirdPlayer, localController.getCurrentPlayer());
    }

    private void addPlayingPlayers(UnoPlayer... players) throws Exception {
        addPlayingPlayers(controller, players);
    }

    private void addPlayingPlayers(GameControllerImpl gameController, Player... players) throws Exception {
        for (Player player : players) {
            gameController.addPlayer(player);
            player.setPlayerStatus(PlayerStatus.PLAYING);
        }
    }

    private void testAddPlayingPlayers(GameControllerImpl gameController, UnoPlayer... players) throws Exception {
        for (UnoPlayer player : players) {
            gameController.addPlayer(player);
            player.setPlayerStatus(PlayerStatus.PLAYING);
        }
    }

    private static class StubDeck implements Deck {
        private final Deque<Card> cardsToDraw = new ArrayDeque<>();
        private Card topCard;

        StubDeck(Card topCard, Card... cardsToDraw) {
            this.topCard = topCard;
            addDrawCards(cardsToDraw);
        }

        void addDrawCards(int count) {
            for (int id = 100; id < 100 + count; id++) {
                cardsToDraw.add(Card.of(CardColour.BLUE, CardType.ONE, id));
            }
        }

        void addDrawCards(Card... cards) {
            for (Card card : cards) {
                cardsToDraw.add(card);
            }
        }

        @Override
        public Card drawCard() {
            return cardsToDraw.removeFirst();
        }

        @Override
        public void playCard(Card card) {
            topCard = card;
        }

        @Override
        public Card getLastPlayedCard() {
            return topCard;
        }
    }

    private static class MutablePlayer implements Player {
        private final String name;
        private final int id;
        private final List<Card> cards = new ArrayList<>();
        private PlayerStatus status = PlayerStatus.NOT_IN_GAME;
        private boolean hasWon;
        private boolean hasSaidUno;

        MutablePlayer(String name, int id) {
            this.name = name;
            this.id = id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void addCard(Card card) {
            cards.add(card);
        }

        @Override
        public List<Card> getCards() {
            return cards;
        }

        @Override
        public Card playCard(int cardId) {
            return cards.stream().filter(card -> card.getCardID() == cardId).findFirst()
                    .map(card -> {
                        cards.remove(card);
                        return card;
                    })
                    .orElseThrow();
        }

        @Override
        public boolean hasUno() {
            return cards.size() == 1;
        }

        @Override
        public void sayUno() {
            hasSaidUno = true;
        }

        @Override
        public boolean hasSaidUno() {
            return hasSaidUno;
        }

        @Override
        public void resetUnoStatus() {
            hasSaidUno = false;
        }

        @Override
        public boolean hasWon() {
            return hasWon;
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
            return status;
        }

        @Override
        public int getId() {
            return id;
        }
    }
}
