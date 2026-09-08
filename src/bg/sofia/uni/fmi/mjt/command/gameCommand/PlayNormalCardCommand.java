package bg.sofia.uni.fmi.mjt.command.gameCommand;

import bg.sofia.uni.fmi.mjt.exception.CannotPlayCardException;
import bg.sofia.uni.fmi.mjt.exception.CardNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.NoColourSelectedException;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.server.exception.ExceptionLogger;

import java.io.IOException;

public class PlayNormalCardCommand implements GameCommand {

    private final GameController controller;
    private final int playerId;
    private final int cardId;

    public PlayNormalCardCommand(GameController controller, int playerId, int cardId) {
        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }
        this.controller = controller;
        this.cardId = cardId;
        this.playerId = playerId;
    }

    @Override
    public String execute() throws IOException, UnoUserException {
        String card;
        try {
            card = controller.playCard(playerId, cardId);
        } catch (CardNotFoundException e) {
            ExceptionLogger.logGameException(e);
            throw new UnoUserException("No such card in your deck", e);
        } catch (PlayerNotFoundException e) {
            ExceptionLogger.logGameException(e);
            throw new UnoUserException("player not found", e);
        } catch (NoColourSelectedException e) {
            ExceptionLogger.logGameException(e);
            throw new UnoUserException("Card needs to have colour selected", e);
        } catch (CannotPlayCardException e) {
            ExceptionLogger.logGameException(e);
            throw new UnoUserException("Cannot play card", e);
        }
        controller.nextTurn();
        return card;
    }
}
