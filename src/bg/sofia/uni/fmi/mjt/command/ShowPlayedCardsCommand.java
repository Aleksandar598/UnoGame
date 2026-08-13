package bg.sofia.uni.fmi.mjt.command;

import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;

public class ShowPlayedCardsCommand implements Command {

    private final GameController controller;
    private final int playerId;
    public ShowPlayedCardsCommand(GameController controller, int playerId) {
        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }

        this.controller = controller;
        this.playerId = playerId;
    }

    @Override
    public String execute() throws UnoUserException {
        if (!controller.hasStarted()) {
            throw new UnoUserException("Game has not started yet");
        }
        return controller.getPlayedCardsLog();
    }
}
