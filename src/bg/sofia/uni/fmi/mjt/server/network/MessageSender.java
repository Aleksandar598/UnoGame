package bg.sofia.uni.fmi.mjt.server.network;

import java.nio.channels.SocketChannel;

public interface MessageSender {
    void send(SocketChannel channel, String message);
}
