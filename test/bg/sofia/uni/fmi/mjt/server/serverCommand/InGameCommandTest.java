package bg.sofia.uni.fmi.mjt.server.serverCommand;

import bg.sofia.uni.fmi.mjt.command.serverCommand.GameCommandCreator;
import bg.sofia.uni.fmi.mjt.command.serverCommand.InGameCommand;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.command.gameCommand.GameCommand;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.server.game.GameManager;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InGameCommandTest {
    private static final String EXPECTED_PLAYER_NOT_IN_GAME = "You are not currently in a game";
    private static final String EXPECTED_PLAYER_NOT_CREATED = "User not created";
    private static final String EXPECTED_GAME_COMMAND_RESPONSE = "Your hand";
    UserManager userManager;
    GameManager gameManager;

    @BeforeEach
    void setUp() {
        this.userManager = Mockito.mock(UserManager.class);
        this.gameManager = Mockito.mock(GameManager.class);
    }

    @Test
    void returnsExpectedPlayerNotInGameWhenPlayerHasNoGame() throws Exception {
        Player player = Mockito.mock(Player.class);
        SocketChannel socket = Mockito.mock(SocketChannel.class);
        when(userManager.getPlayer(socket)).thenReturn(player);
        when(player.getId()).thenReturn(7);
        when(gameManager.isInGame(7)).thenReturn(false);
        String response = command(userManager, gameManager, null).execute("show-hand", socket);
        assertEquals(EXPECTED_PLAYER_NOT_IN_GAME, response);
        verify(gameManager, never()).getUserGame(player);
    }

    @Test
    void returnsExpectedPlayerNotCreatedWhenUserManagerRejectsSocket() throws Exception {
        SocketChannel socket = Mockito.mock(SocketChannel.class);
        when(userManager.getPlayer(socket)).thenThrow(new UnoUserException(EXPECTED_PLAYER_NOT_CREATED));
        String response = command(userManager, gameManager, null).execute("show-hand", socket);
        assertEquals(EXPECTED_PLAYER_NOT_CREATED, response);
    }

    @Test
    void returnsExpectedGameCommandResponseAndMarksEndedGame() throws Exception {
        Player player = Mockito.mock(Player.class);
        GameController controller = Mockito.mock(GameController.class);
        GameCommand gameCommand = Mockito.mock(GameCommand.class);
        SocketChannel socket = Mockito.mock(SocketChannel.class);
        when(userManager.getPlayer(socket)).thenReturn(player);
        when(player.getId()).thenReturn(7);
        when(gameManager.isInGame(7)).thenReturn(true);
        when(gameManager.getUserGame(player)).thenReturn(controller);
        when(gameCommand.execute()).thenReturn(EXPECTED_GAME_COMMAND_RESPONSE);
        when(controller.hasEnded()).thenReturn(true);

        String response = command(userManager, gameManager, (input, usedController, playerId) -> gameCommand)
                .execute("show-hand", socket);

        assertEquals(EXPECTED_GAME_COMMAND_RESPONSE, response);
        verify(gameManager).markGameAsEnded(player);
    }

    private InGameCommand command(
            UserManager userManager, GameManager gameManager, GameCommandCreator gameCommandCreator) {
        GameCommandCreator creator = gameCommandCreator == null
                ? (input, controller, playerId) -> Mockito.mock(GameCommand.class)
                : gameCommandCreator;
        return new InGameCommand(userManager, gameManager, creator);
    }
}
