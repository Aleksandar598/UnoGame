package bg.sofia.uni.fmi.mjt.game.command;

import bg.sofia.uni.fmi.mjt.exception.CannotPlayCardException;
import bg.sofia.uni.fmi.mjt.exception.CardNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.GameNotStartedException;
import bg.sofia.uni.fmi.mjt.exception.NoColourSelectedException;
import bg.sofia.uni.fmi.mjt.exception.NotAColourChangeCardException;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.game.card.CardColour;

import bg.sofia.uni.fmi.mjt.server.exception.ExceptionLogger;

import java.io.IOException;

public class PlaySpecialCardCommand implements GameCommand {

    private final GameController controller;
    private final int playerId;
    private final int cardId;
    private final CardColour colour;

    public PlaySpecialCardCommand(GameController controller, int playerId, int cardId, CardColour colour) {
        this.cardId = cardId;
        this.playerId = playerId;

        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }

        if (colour == null) {
            throw new IllegalArgumentException("colour cannot be null");
        }

        this.colour = colour;
        this.controller = controller;
    }

    @Override
    public String execute() throws UnoUserException, IOException {
        String card;
        try {
            card = controller.playCard(playerId, cardId, colour);
        } catch (CardNotFoundException | NoColourSelectedException | NotAColourChangeCardException |
                 PlayerNotFoundException | CannotPlayCardException | GameNotStartedException e) {
            ExceptionLogger.logGameException(e);
            throw new UnoUserException(e.getMessage(), e);
        }

        controller.nextTurn();

        return card;
    }
}
