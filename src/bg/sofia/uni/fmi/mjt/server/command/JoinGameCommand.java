package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.exception.GameHasStartedException;
import bg.sofia.uni.fmi.mjt.exception.MaximumPlayerCountReached;
import bg.sofia.uni.fmi.mjt.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.server.exception.ExceptionLogger;
import bg.sofia.uni.fmi.mjt.server.exception.GameNotFoundException;
import bg.sofia.uni.fmi.mjt.server.exception.MissingArgumentsException;
import bg.sofia.uni.fmi.mjt.server.game.GameManager;
import bg.sofia.uni.fmi.mjt.server.game.GameManagerImpl;
import bg.sofia.uni.fmi.mjt.server.user.UserManager;
import bg.sofia.uni.fmi.mjt.server.user.UserManagerImpl;
import bg.sofia.uni.fmi.mjt.server.command.parser.ArgumentsParser;
import bg.sofia.uni.fmi.mjt.server.command.parser.ArgumentsParserImpl;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Map;

public class JoinGameCommand extends AbstractCommand {

    String gameId;
    String displayName;
    private final UserManager userManager;
    private final GameManager gameManager;
    private final ArgumentsParser argumentsParser;

    private static final String NOT_LOGGED = "You are not logged in";
    private static final String MAX_PLAYER_COUNT = "Game you want to join is full";
    private static final String GAME_STARTED = "Game has already started";
    private static final String GAME_NOT_FOUND = "Game has not been found";
    private static final String SUCCESS = "successfully joined game";
    private static final String INVALID_ARGS = "Invalid arguments";

    public JoinGameCommand() {
        this(UserManagerImpl.getInstance(), GameManagerImpl.getInstance(), new ArgumentsParserImpl());
    }

    JoinGameCommand(UserManager userManager, GameManager gameManager, ArgumentsParser argumentsParser) {
        if (userManager == null || gameManager == null || argumentsParser == null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }
        this.userManager = userManager;
        this.gameManager = gameManager;
        this.argumentsParser = argumentsParser;
    }

    @Override
    public String execute(String input, SocketChannel socket) throws IOException {
        try {
            parse(input, socket);
            Player player = userManager.bindSocketToPlayer(socket, displayName);
            gameManager.joinGame(gameId, player);
            gameManager.notifyAllInAGame(player, player.getName() + SUCCESS );

        } catch (UserNotLoggedInException e) {
            ExceptionLogger.logException(e);
            return NOT_LOGGED;
        } catch (MaximumPlayerCountReached e) {
            userManager.unbindPlayer(socket);
            ExceptionLogger.logException(e);
            return MAX_PLAYER_COUNT;

        } catch (GameHasStartedException e) {
            userManager.unbindPlayer(socket);
            ExceptionLogger.logException(e);
            return GAME_STARTED;
        } catch (GameNotFoundException e) {
            userManager.unbindPlayer(socket);
            ExceptionLogger.logException(e);
            return GAME_NOT_FOUND;
        } catch (MissingArgumentsException e) {
            ExceptionLogger.logException(e);
            return INVALID_ARGS;
        }
        return SUCCESS;
    }

    private void parse(String input, SocketChannel socket) throws MissingArgumentsException, UserNotLoggedInException {
        Map<String, String> args = argumentsParser.parse(input);

        if (!args.containsKey("game-id")) {
            throw new MissingArgumentsException("Need gameid and displayname to join a game");
        }
        if (args.containsKey("display-name")) {
            displayName = args.get("display-name");
        } else {
            displayName = userManager.getUsername(socket);
        }
        gameId = args.get("game-id");
    }
}
