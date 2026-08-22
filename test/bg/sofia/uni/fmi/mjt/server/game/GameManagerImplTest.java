package bg.sofia.uni.fmi.mjt.server.game;

import bg.sofia.uni.fmi.mjt.exception.GameHasStartedException;
import bg.sofia.uni.fmi.mjt.exception.MaximumPlayerCountReached;
import bg.sofia.uni.fmi.mjt.exception.CannotStartGameException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.server.exception.GameNotFoundException;
import bg.sofia.uni.fmi.mjt.server.exception.UserInGameException;
import bg.sofia.uni.fmi.mjt.server.network.MessageSender;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;
import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class GameManagerImplTest {
    private static final String GAME_ID = "game1";
    private static final String UNKNOWN_GAME_ID = "unknown-game";
    private static final String CREATOR_NAME = "creator";
    private static final int PLAYER_COUNT = 4;
    private static final int FIRST_PLAYER_ID = 1;
    private static final int SECOND_PLAYER_ID = 2;
    private static final int THIRD_PLAYER_ID = 3;
    private static final String EXPECTED_DUPLICATE_GAME_ID = "Game ID already exists";
    private static final String EXPECTED_GAME_NOT_FOUND = "Game with id " + UNKNOWN_GAME_ID + " does not exist";
    private static final String EXPECTED_USER_ALREADY_IN_GAME = "User is already in game";
    private static final String EXPECTED_MAXIMUM_PLAYER_COUNT = "Maximum player count reached";
    private static final String EXPECTED_GAME_HAS_STARTED = "Cannot join game in progress";
    private static final String EXPECTED_CANNOT_START_GAME = "Cannot start game without at least 2 players";
    private static final String EXPECTED_PLAYER_NOT_CREATOR = "Player is not the creator";
    private static final String EXPECTED_PLAYER_HAS_NO_GAME = "Game not found";
    private static final String EXPECTED_PLAYER_NOT_IN_GAME = "Player is not in a game";
    private static final String BROADCAST_MESSAGE = "Game has started";

    private GameManagerImpl gameManager;
    private UserManager userManager;
    private MessageSender messageSender;

    @BeforeEach
    void setUp() {
        userManager = Mockito.mock(UserManager.class);
        messageSender = Mockito.mock(MessageSender.class);
        gameManager = new GameManagerImpl(userManager, messageSender);
    }

    @Test
    void testCreateGameAddsAvailableGameWithExpectedInformation() throws Exception {
        gameManager.createGame(GAME_ID, CREATOR_NAME, PLAYER_COUNT);

        GameInfo gameInfo = gameManager.getGame(GAME_ID);
        GameController controller = gameManager.getGameController(GAME_ID);

        assertEquals(GAME_ID, gameInfo.gameId());
        assertEquals(CREATOR_NAME, gameInfo.creatorUsername());
        assertEquals(GameStatus.AVAILABLE, gameInfo.status());
        assertEquals(List.of(), gameInfo.players());
        assertEquals(PLAYER_COUNT, gameInfo.maxPlayerCount());
        assertEquals(List.of(gameInfo), gameManager.listGames());
        assertNotNull(controller);
    }

    @Test
    void testCreateGameRejectsDuplicateGameId() {
        gameManager.createGame(GAME_ID, CREATOR_NAME, PLAYER_COUNT);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> gameManager.createGame(GAME_ID, "other-creator", 2));

        assertEquals(EXPECTED_DUPLICATE_GAME_ID, exception.getMessage());
    }

    @Test
    void testGetGameReturnsCreatedGame() throws Exception {
        gameManager.createGame(GAME_ID, CREATOR_NAME, PLAYER_COUNT);

        GameInfo gameInfo = gameManager.getGame(GAME_ID);

        assertEquals(GAME_ID, gameInfo.gameId());
        assertEquals(CREATOR_NAME, gameInfo.creatorUsername());
    }

    @Test
    void testGetGameThrowsWhenGameDoesNotExist() {
        GameNotFoundException exception = assertThrows(GameNotFoundException.class,
                () -> gameManager.getGame(UNKNOWN_GAME_ID));

        assertEquals(UNKNOWN_GAME_ID, exception.getMessage());
    }

    @Test
    void testJoinGameAddsPlayerToGameAndRegistersMembership() throws Exception {
        Player player = player(FIRST_PLAYER_ID);
        gameManager.createGame(GAME_ID, CREATOR_NAME, PLAYER_COUNT);

        gameManager.joinGame(GAME_ID, player);

        GameInfo gameInfo = gameManager.getGame(GAME_ID);
        assertEquals(List.of(FIRST_PLAYER_ID), gameInfo.players());
        assertEquals(GameStatus.AVAILABLE, gameInfo.status());
        assertTrue(gameManager.isInGame(FIRST_PLAYER_ID));
        assertEquals(gameManager.getGameController(GAME_ID), gameManager.getUserGame(player));
    }

    @Test
    void testJoinGameThrowsWhenGameDoesNotExist() {
        Player player = player(FIRST_PLAYER_ID);

        GameNotFoundException exception = assertThrows(GameNotFoundException.class,
                () -> gameManager.joinGame(UNKNOWN_GAME_ID, player));

        assertEquals(EXPECTED_GAME_NOT_FOUND, exception.getMessage());
    }

    @Test
    void testJoinGameRejectsPlayerWhoIsAlreadyInAGame() throws Exception {
        Player player = player(FIRST_PLAYER_ID);
        gameManager.createGame(GAME_ID, CREATOR_NAME, PLAYER_COUNT);
        gameManager.joinGame(GAME_ID, player);

        UserInGameException exception = assertThrows(UserInGameException.class,
                () -> gameManager.joinGame(GAME_ID, player));

        assertEquals(EXPECTED_USER_ALREADY_IN_GAME, exception.getMessage());
    }

    @Test
    void testJoinGameRejectsPlayerWhenLobbyIsFull() throws Exception {
        Player firstPlayer = player(FIRST_PLAYER_ID);
        Player secondPlayer = player(SECOND_PLAYER_ID);
        gameManager.createGame(GAME_ID, CREATOR_NAME, 1);
        gameManager.joinGame(GAME_ID, firstPlayer);

        MaximumPlayerCountReached exception = assertThrows(MaximumPlayerCountReached.class,
                () -> gameManager.joinGame(GAME_ID, secondPlayer));

        assertEquals(EXPECTED_MAXIMUM_PLAYER_COUNT, exception.getMessage());
    }

    @Test
    void testJoinGameRejectsPlayerWhenGameHasStarted() throws Exception {
        Player firstPlayer = player(FIRST_PLAYER_ID);
        Player secondPlayer = player(SECOND_PLAYER_ID);
        Player thirdPlayer = player(THIRD_PLAYER_ID);
        gameManager.createGame(GAME_ID, CREATOR_NAME, 3);
        gameManager.joinGame(GAME_ID, firstPlayer);
        gameManager.joinGame(GAME_ID, secondPlayer);
        gameManager.startGame(firstPlayer, CREATOR_NAME);

        GameHasStartedException exception = assertThrows(GameHasStartedException.class,
                () -> gameManager.joinGame(GAME_ID, thirdPlayer));

        assertEquals(EXPECTED_GAME_HAS_STARTED, exception.getMessage());
    }

    @Test
    void testStartGameStartsControllerAndUpdatesGameStatus() throws Exception {
        Player creator = player(FIRST_PLAYER_ID);
        Player secondPlayer = player(SECOND_PLAYER_ID);
        gameManager.createGame(GAME_ID, CREATOR_NAME, PLAYER_COUNT);
        gameManager.joinGame(GAME_ID, creator);
        gameManager.joinGame(GAME_ID, secondPlayer);

        gameManager.startGame(creator, CREATOR_NAME);

        assertEquals(GameStatus.STARTED, gameManager.getGame(GAME_ID).status());
        assertTrue(gameManager.getGameController(GAME_ID).hasStarted());
    }

    @Test
    void testStartGameRejectsGameWithFewerThanTwoPlayers() throws Exception {
        Player creator = player(FIRST_PLAYER_ID);
        gameManager.createGame(GAME_ID, CREATOR_NAME, PLAYER_COUNT);
        gameManager.joinGame(GAME_ID, creator);

        CannotStartGameException exception = assertThrows(CannotStartGameException.class,
                () -> gameManager.startGame(creator, CREATOR_NAME));

        assertEquals(EXPECTED_CANNOT_START_GAME, exception.getMessage());
        assertEquals(GameStatus.AVAILABLE, gameManager.getGame(GAME_ID).status());
    }

    @Test
    void testStartGameRejectsPlayerWhoIsNotTheCreator() throws Exception {
        Player player = player(FIRST_PLAYER_ID);
        gameManager.createGame(GAME_ID, CREATOR_NAME, PLAYER_COUNT);
        gameManager.joinGame(GAME_ID, player);

        UnoUserException exception = assertThrows(UnoUserException.class,
                () -> gameManager.startGame(player, "other-user"));

        assertEquals(EXPECTED_PLAYER_NOT_CREATOR, exception.getMessage());
        assertEquals(GameStatus.AVAILABLE, gameManager.getGame(GAME_ID).status());
    }

    @Test
    void testStartGameRejectsPlayerWhoIsNotInAGame() {
        Player player = player(FIRST_PLAYER_ID);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> gameManager.startGame(player, CREATOR_NAME));

        assertEquals(EXPECTED_PLAYER_HAS_NO_GAME, exception.getMessage());
    }

    @Test
    void testNotifyAllInAGameSendsMessageToEveryConnectedGamePlayer() throws Exception {
        Player firstPlayer = player(FIRST_PLAYER_ID);
        Player secondPlayer = player(SECOND_PLAYER_ID);
        SocketChannel firstSocket = Mockito.mock(SocketChannel.class);
        SocketChannel secondSocket = Mockito.mock(SocketChannel.class);
        gameManager.createGame(GAME_ID, CREATOR_NAME, PLAYER_COUNT);
        gameManager.joinGame(GAME_ID, firstPlayer);
        gameManager.joinGame(GAME_ID, secondPlayer);
        when(userManager.getConnectedSockets(List.of(FIRST_PLAYER_ID, SECOND_PLAYER_ID)))
                .thenReturn(Set.of(firstSocket, secondSocket));

        gameManager.notifyAllInAGame(firstPlayer, BROADCAST_MESSAGE);

        verify(messageSender).send(firstSocket, BROADCAST_MESSAGE);
        verify(messageSender).send(secondSocket, BROADCAST_MESSAGE);
    }

    @Test
    void testNotifyAllInAGameRejectsNullPlayerOrMessage() {
        Player player = player(FIRST_PLAYER_ID);

        assertThrows(IllegalArgumentException.class, () -> gameManager.notifyAllInAGame(null, BROADCAST_MESSAGE));
        assertThrows(IllegalArgumentException.class, () -> gameManager.notifyAllInAGame(player, null));
    }

    @Test
    void testNotifyAllInAGameDoesNothingWhenPlayerHasNoGame() {
        Player player = player(FIRST_PLAYER_ID);

        gameManager.notifyAllInAGame(player, BROADCAST_MESSAGE);

        verifyNoInteractions(userManager, messageSender);
    }

    @Test
    void testLeaveGameRemovesPlayerFromGameAndMembership() throws Exception {
        Player firstPlayer = player(FIRST_PLAYER_ID);
        Player secondPlayer = player(SECOND_PLAYER_ID);
        gameManager.createGame(GAME_ID, CREATOR_NAME, PLAYER_COUNT);
        gameManager.joinGame(GAME_ID, firstPlayer);
        gameManager.joinGame(GAME_ID, secondPlayer);

        gameManager.leaveGame(firstPlayer);

        assertEquals(List.of(SECOND_PLAYER_ID), gameManager.getGame(GAME_ID).players());
        assertFalse(gameManager.isInGame(FIRST_PLAYER_ID));
        assertTrue(gameManager.isInGame(SECOND_PLAYER_ID));
    }

    @Test
    void testLeaveGameRemovesGameWhenLastPlayerLeaves() throws Exception {
        Player player = player(FIRST_PLAYER_ID);
        gameManager.createGame(GAME_ID, CREATOR_NAME, PLAYER_COUNT);
        gameManager.joinGame(GAME_ID, player);

        gameManager.leaveGame(player);

        assertFalse(gameManager.isInGame(FIRST_PLAYER_ID));
        assertThrows(GameNotFoundException.class, () -> gameManager.getGame(GAME_ID));
        assertThrows(GameNotFoundException.class, () -> gameManager.getGameController(GAME_ID));
    }

    @Test
    void testLeaveGameMarksStartedGameAsEndedWhenOnePlayerRemains() throws Exception {
        Player firstPlayer = player(FIRST_PLAYER_ID);
        Player secondPlayer = player(SECOND_PLAYER_ID);
        when(firstPlayer.getPlayerStatus()).thenReturn(bg.sofia.uni.fmi.mjt.player.PlayerStatus.PLAYING);
        when(secondPlayer.getPlayerStatus()).thenReturn(bg.sofia.uni.fmi.mjt.player.PlayerStatus.PLAYING);
        gameManager.createGame(GAME_ID, CREATOR_NAME, PLAYER_COUNT);
        gameManager.joinGame(GAME_ID, firstPlayer);
        gameManager.joinGame(GAME_ID, secondPlayer);
        gameManager.startGame(firstPlayer, CREATOR_NAME);

        gameManager.leaveGame(firstPlayer);

        assertEquals(GameStatus.ENDED, gameManager.getGame(GAME_ID).status());
        assertEquals(List.of(SECOND_PLAYER_ID), gameManager.getGame(GAME_ID).players());
    }

    @Test
    void testLeaveGameRejectsNullPlayer() {
        assertThrows(IllegalArgumentException.class, () -> gameManager.leaveGame(null));
    }

    @Test
    void testLeaveGameRejectsPlayerWhoIsNotInAGame() {
        Player player = player(FIRST_PLAYER_ID);

        UnoUserException exception = assertThrows(UnoUserException.class, () -> gameManager.leaveGame(player));

        assertEquals(EXPECTED_PLAYER_NOT_IN_GAME, exception.getMessage());
    }

    private Player player(int playerId) {
        Player player = Mockito.mock(Player.class);
        when(player.getId()).thenReturn(playerId);
        return player;
    }
}
