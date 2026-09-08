package bg.sofia.uni.fmi.mjt.command.gameCommand;

import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;

import java.io.IOException;

public class GetCurrentColourCommand implements GameCommand {
    private final GameController controller;

    public GetCurrentColourCommand(GameController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }
        this.controller = controller;
    }

    @Override
    public String execute() throws UnoUserException, IOException {
        return controller.getCurrentColour().toString();
    }
}
