package bg.sofia.uni.fmi.mjt.game.command;

import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;

public class ShowPlayedCardsCommand implements GameCommand {

    private final GameController controller;

    public ShowPlayedCardsCommand(GameController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }

        this.controller = controller;
    }

    @Override
    public String execute() throws UnoUserException {
        if (!controller.hasStarted()) {
            throw new UnoUserException("Game has not started yet");
        }
        return controller.getPlayedCardsLog();
    }
}
