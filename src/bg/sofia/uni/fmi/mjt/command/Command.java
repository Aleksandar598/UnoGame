package bg.sofia.uni.fmi.mjt.command;

import bg.sofia.uni.fmi.mjt.exception.UnoUserException;

public interface Command {

    String execute() throws UnoUserException;
}
