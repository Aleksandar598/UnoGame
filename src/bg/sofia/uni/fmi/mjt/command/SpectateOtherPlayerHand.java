package bg.sofia.uni.fmi.mjt.command;

import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.game.card.Card;
import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.player.PlayerStatus;

public class SpectateOtherPlayerHand implements Command {
    private final int wantedPlayer;
    private final int playerId;
    private final GameController controller;

    public SpectateOtherPlayerHand(GameController controller, int playerId, int wantedPlayer) {
        this.playerId = playerId;
        this.wantedPlayer = wantedPlayer;
        if (controller == null) {
            throw new IllegalArgumentException("controller cannot be null");
        }
        this.controller = controller;
    }

    @Override
    public String execute() throws UnoUserException {
        try {
            Player spectator = controller.getPlayer(playerId);
            if (spectator.getPlayerStatus() != PlayerStatus.SPECTATING) {
                throw new UnoUserException("you cannot spectate");
            }
            Player toSpectate = controller.getPlayer(this.wantedPlayer);

            if (toSpectate.getPlayerStatus() != PlayerStatus.PLAYING) {
                throw new UnoUserException("Player is not playing");
            }
            StringBuilder builder = new StringBuilder();
            int iter = 1;
            for (Card c : toSpectate.getCards()) {
                builder.append(iter++)
                        .append(". ")
                        .append(c.getCardAsString())
                        .append(System.lineSeparator());
            }
            return builder.toString();
        } catch (PlayerNotFoundException e) {
            throw new UnoUserException("Player not found", e);
        }
    }
}
