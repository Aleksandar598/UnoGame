package bg.sofia.uni.fmi.mjt.command.serverCommand;

import bg.sofia.uni.fmi.mjt.command.gameCommand.GameCommand;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;

@FunctionalInterface
interface GameCommandCreator {
    GameCommand create(String input, GameController controller, int playerId);
}
