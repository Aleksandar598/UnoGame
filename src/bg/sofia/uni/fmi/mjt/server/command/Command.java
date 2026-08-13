package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.exception.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.exception.WrongUserCredentialsException;
import bg.sofia.uni.fmi.mjt.server.exception.MissingArgumentsException;

import java.nio.channels.SocketChannel;

public interface Command {

    void execute(String input, SocketChannel socket) throws MissingArgumentsException, WrongUserCredentialsException, UserAlreadyExistsException;
}
