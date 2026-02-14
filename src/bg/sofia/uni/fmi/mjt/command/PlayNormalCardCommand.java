package bg.sofia.uni.fmi.mjt.command;

import bg.sofia.uni.fmi.mjt.GameController;
import bg.sofia.uni.fmi.mjt.exception.CardNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;

public class PlayNormalCardCommand implements Command {

    private final GameController controller;
    private final int playerId;
    private final int cardId;
    private static final String SUCCESS_STRING = "Success";

    public PlayNormalCardCommand(GameController controller, int playerId, int cardId) {
        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }
        this.controller = controller;
        this.cardId = cardId;
        this.playerId = playerId;
    }
    // add turn change in GameController

    @Override
    public String execute() throws UnoUserException {
        if (playerId != controller.getCurrentPlayer().getId()) {
            throw new UnoUserException("It is not your turn");
        }

        try {
            controller.playCard(playerId, cardId);
        } catch (CardNotFoundException e) {
            throw new UnoUserException("No such card in your deck", e);
        } catch (PlayerNotFoundException e) {
            throw new IllegalArgumentException("player not found", e);
        }

        return SUCCESS_STRING;
    }
}
