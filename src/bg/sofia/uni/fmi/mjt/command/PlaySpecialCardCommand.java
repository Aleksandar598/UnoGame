package bg.sofia.uni.fmi.mjt.command;

import bg.sofia.uni.fmi.mjt.GameController;
import bg.sofia.uni.fmi.mjt.card.CardColour;
import bg.sofia.uni.fmi.mjt.exception.CardNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.NotAColourChangeCardException;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;

public class PlaySpecialCardCommand implements Command {

    private final GameController controller;
    private final int playerId;
    private final int cardId;
    private final CardColour colour;
    private static final String SUCCESS_STRING = "Success";

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
    public String execute() throws UnoUserException {
        if (this.playerId != controller.getCurrentPlayer().getId()) {
            throw new UnoUserException("It is not your turn!");
        }

        try {
            controller.playCard(playerId, cardId, colour);
        } catch (CardNotFoundException e) {
            throw new UnoUserException("No such card in your deck", e);
        } catch (PlayerNotFoundException e) {
            throw new IllegalArgumentException("player not found", e);
        } catch (NotAColourChangeCardException e) {
            throw new UnoUserException("Card cannot change colour!");
        }

        return SUCCESS_STRING;
    }
}
