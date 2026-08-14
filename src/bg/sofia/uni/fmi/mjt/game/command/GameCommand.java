package bg.sofia.uni.fmi.mjt.game.command;

import bg.sofia.uni.fmi.mjt.exception.UnoUserException;

public interface GameCommand {

    String execute() throws UnoUserException;
}
