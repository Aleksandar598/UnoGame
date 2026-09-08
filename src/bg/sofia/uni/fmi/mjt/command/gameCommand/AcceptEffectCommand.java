package bg.sofia.uni.fmi.mjt.command.gameCommand;

import bg.sofia.uni.fmi.mjt.exception.CannotPlayCardException;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.server.exception.ExceptionLogger;

import java.io.IOException;

public class AcceptEffectCommand implements GameCommand {
    private final  int playerId;
    private final GameController controller;
    
    public AcceptEffectCommand(GameController controller, int playerId) {
        this.playerId = playerId;
        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }
        this.controller = controller;
    }

    @Override
    public String execute() throws UnoUserException, IOException {

        StringBuilder response = new StringBuilder();
        response.append("Drawn ")
                .append(controller.penaltyCardCount())
                .append(" cards:")
                .append(System.lineSeparator());

        try {
            String str = controller.acceptPenalty(this.playerId);
            response.append(str);
        } catch (PlayerNotFoundException | CannotPlayCardException e) {
            ExceptionLogger.logGameException(e);
            throw new UnoUserException(e.getMessage(), e);
        }
        return response.toString();
    }
}
