package bg.sofia.uni.fmi.mjt.game.command;

import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.game.card.CardColour;
import bg.sofia.uni.fmi.mjt.exception.*;

public class PlaySpecialCardCommand implements GameCommand {

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

        if (!controller.hasStarted()) {
            throw new UnoUserException("Game has not started yet");
        }

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
            throw new UnoUserException("Card cannot change colour!", e);
        } catch (NoColourSelectedException e) {
            throw new UnoUserException("You have not selected a colour", e);
        } catch (CannotPlayCardException e) {
            throw new UnoUserException("Cannot play card", e);
        }

        controller.nextTurn();

        return SUCCESS_STRING;
    }
}
