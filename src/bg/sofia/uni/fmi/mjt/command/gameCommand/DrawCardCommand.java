package bg.sofia.uni.fmi.mjt.command.gameCommand;

import bg.sofia.uni.fmi.mjt.exception.CannotPlayCardException;
import bg.sofia.uni.fmi.mjt.exception.GameNotStartedException;
import bg.sofia.uni.fmi.mjt.exception.InvalidActionException;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.game.card.Card;
import bg.sofia.uni.fmi.mjt.server.exception.ExceptionLogger;

import java.io.IOException;

public class DrawCardCommand implements GameCommand {
    private final int playerId;
    private final GameController controller;

    public DrawCardCommand(int playerId, GameController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }
        this.playerId = playerId;
        this.controller = controller;
    }

    @Override
    public String execute() throws UnoUserException, IOException {
        try {
            Card card = controller.drawCard(playerId);
            return "Card drawn: " + card.getCardAsString();
        } catch (PlayerNotFoundException | CannotPlayCardException |
                 GameNotStartedException | InvalidActionException e) {
            ExceptionLogger.logException(e);
            throw new UnoUserException(e.getMessage(), e);
        }
    }
}

