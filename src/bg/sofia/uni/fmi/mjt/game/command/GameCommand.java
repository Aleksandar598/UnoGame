package bg.sofia.uni.fmi.mjt.game.command;

import bg.sofia.uni.fmi.mjt.exception.UnoUserException;

import java.io.IOException;

public interface GameCommand {

    String execute() throws UnoUserException, IOException;
}
