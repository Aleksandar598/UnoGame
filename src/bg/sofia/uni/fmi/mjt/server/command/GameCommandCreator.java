package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.game.command.GameCommand;
import bg.sofia.uni.fmi.mjt.game.controller.GameController;

@FunctionalInterface
interface GameCommandCreator {
    GameCommand create(String input, GameController controller, int playerId);
}
