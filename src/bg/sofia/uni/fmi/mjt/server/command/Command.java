package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.exception.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.exception.WrongUserCredentialsException;
import bg.sofia.uni.fmi.mjt.server.exception.MissingArgumentsException;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface Command {

    String execute(String input, SocketChannel socket) throws IOException;
}
