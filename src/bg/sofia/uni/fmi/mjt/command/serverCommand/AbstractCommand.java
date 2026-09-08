package bg.sofia.uni.fmi.mjt.command.serverCommand;

import bg.sofia.uni.fmi.mjt.command.serverCommand.parser.ArgumentsParser;
import bg.sofia.uni.fmi.mjt.command.serverCommand.parser.ArgumentsParserImpl;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public abstract class AbstractCommand implements Command {

    protected static final ArgumentsParser PARSER = new ArgumentsParserImpl();

    public abstract String execute(String input, SocketChannel socket) throws IOException;

}
