package bg.sofia.uni.fmi.mjt.command.serverCommand;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface Command {

    String execute(String input, SocketChannel socket) throws IOException;
}
