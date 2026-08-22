package bg.sofia.uni.fmi.mjt.server.network;

import java.nio.channels.SocketChannel;

public class ServerMessageGateway {
    private static volatile MessageSender sender;

    private ServerMessageGateway() { }

    public static void setSender(MessageSender messageSender) {
        sender = messageSender;
    }

    public static void clearSender(MessageSender messageSender) {
        if (sender == messageSender) {
            sender = null;
        }
    }

    public static void send(SocketChannel channel, String message) {
        MessageSender currentSender = sender;
        if (currentSender != null && channel != null && message != null) {
            currentSender.send(channel, message);
        }
    }
}
