package bg.sofia.uni.fmi.mjt.game.command;

import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.player.PlayerStatus;

public class SpectateCommand implements GameCommand {

    private int playerId;
    private GameController controller;
    private static final String SUCESS_STRING = "Now spectating";

    public SpectateCommand(GameController controller, int playerId) {
        this.playerId = playerId;

        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }
        this.controller = controller;
    }

    @Override
    public String execute() throws UnoUserException {
        try {
            if (!controller.hasStarted()) {
                throw new UnoUserException("Game has not started yet");
            }
            Player player = controller.getPlayer(this.playerId);

            if (!player.hasWon()) {
                throw new UnoUserException("You are still in the game!");
            }
            player.setPlayerStatus(PlayerStatus.SPECTATING);
        } catch (PlayerNotFoundException e) {
            throw new UnoUserException("You are not in the game", e);
        }
        return SUCESS_STRING;
    }
}
