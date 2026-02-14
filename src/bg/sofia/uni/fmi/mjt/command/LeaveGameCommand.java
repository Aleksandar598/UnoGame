package bg.sofia.uni.fmi.mjt.command;

import bg.sofia.uni.fmi.mjt.GameController;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;

public class LeaveGameCommand implements Command {

    private final int playerId;
    private final GameController controller;
    private static final String SUCCESS_STRING = "Successfully left game";

    public LeaveGameCommand(GameController controller, int playerId) {
        this.playerId = playerId;

        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }
        this.controller = controller;
    }

    @Override
    public String execute() throws UnoUserException {
        try {
            controller.removePlayer(this.playerId);
        } catch (PlayerNotFoundException e) {
            throw new UnoUserException("Player is not found in the game", e);
        }
        controller.nextTurn();
        return SUCCESS_STRING;
    }
}
