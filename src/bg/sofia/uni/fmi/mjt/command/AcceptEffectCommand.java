package bg.sofia.uni.fmi.mjt.command;

import bg.sofia.uni.fmi.mjt.controller.GameController;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;

public class AcceptEffectCommand implements  Command {
    private final  int playerId;
    private final GameController controller;
    
    AcceptEffectCommand(GameController controller, int playerId) {
        this.playerId = playerId;
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
        if (!this.controller.isTherePendingCardDraw()) {
            throw new UnoUserException("There is no penalty pending");
        }

        if (this.playerId != this.controller.getCurrentPlayer().getId()) {
            throw new UnoUserException("It is not your turn to accept the penalty");
        }
        StringBuilder response = new StringBuilder();
        response.append("Drawn ")
                .append(controller.penaltyCardCount())
                .append(" cards:")
                .append(System.lineSeparator());

        try {
            String str =  controller.acceptPenalty(this.playerId);
            response.append(str);
        } catch (PlayerNotFoundException e) {
            throw new IllegalArgumentException("Player not found", e);
        }
        controller.nextTurn();
        return response.toString();
    }
}
