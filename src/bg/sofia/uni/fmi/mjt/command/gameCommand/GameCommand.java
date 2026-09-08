package bg.sofia.uni.fmi.mjt.command.gameCommand;

import bg.sofia.uni.fmi.mjt.exception.UnoUserException;

import java.io.IOException;

public interface GameCommand {

    String execute() throws UnoUserException, IOException;
}
