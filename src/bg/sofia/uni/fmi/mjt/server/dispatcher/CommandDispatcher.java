package bg.sofia.uni.fmi.mjt.server.dispatcher;

import java.nio.channels.SocketChannel;

public interface CommandDispatcher {

    String dispatch(String input, SocketChannel socket);
}
