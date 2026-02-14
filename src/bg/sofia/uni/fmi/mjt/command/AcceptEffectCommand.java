package bg.sofia.uni.fmi.mjt.command;

import bg.sofia.uni.fmi.mjt.GameController;
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
        if (!this.controller.isTherePendingCardDraw()) {
            throw new UnoUserException("There is no penalty pending");
        }

        if (this.playerId != this.controller.getCurrentPlayer().getId()) {
            throw new UnoUserException("It is not your turn to accept the penalty");
        }
        String response = "Drawn " +
                controller.penaltyCardCount() +
                " cards";

        try {
            controller.acceptPenalty(this.playerId);
        } catch (PlayerNotFoundException e) {
            throw new IllegalArgumentException("Player not found", e);
        }
        controller.resetPenalty();
        controller.nextTurn();

        return response;
    }
}
