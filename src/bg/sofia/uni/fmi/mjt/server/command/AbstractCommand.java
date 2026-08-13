package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.exception.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.exception.WrongUserCredentialsException;
import bg.sofia.uni.fmi.mjt.server.command.parser.ArgumentsParser;
import bg.sofia.uni.fmi.mjt.server.command.parser.ArgumentsParserImpl;
import bg.sofia.uni.fmi.mjt.server.exception.MissingArgumentsException;

import java.nio.channels.SocketChannel;

public abstract class AbstractCommand implements Command {

    protected static final ArgumentsParser parser = new ArgumentsParserImpl();

    public abstract void execute(String input, SocketChannel socket) throws MissingArgumentsException, WrongUserCredentialsException, UserAlreadyExistsException;

}
