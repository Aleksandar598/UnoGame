package bg.sofia.uni.fmi.mjt.server;

import bg.sofia.uni.fmi.mjt.exception.PlayerNotFoundException;
import bg.sofia.uni.fmi.mjt.exception.UnoUserException;
import bg.sofia.uni.fmi.mjt.player.Player;
import bg.sofia.uni.fmi.mjt.server.game.GameManagerImpl;
import bg.sofia.uni.fmi.mjt.server.dispatcher.CommandDispatcher;
import bg.sofia.uni.fmi.mjt.server.dispatcher.CommandDispatcherImpl;
import bg.sofia.uni.fmi.mjt.server.network.MessageSender;
import bg.sofia.uni.fmi.mjt.server.network.ServerMessageGateway;
import bg.sofia.uni.fmi.mjt.server.user.UserManagerImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

public class UnoServer implements Runnable, AutoCloseable, MessageSender {
    private static final int BUFFER_SIZE = 8 * 1024;
    private static final int MIN_PORT = 1;
    private static final int MAX_PORT = 65535;

    private final Selector selector;
    private final ServerSocketChannel serverChannel;
    private final CommandDispatcher dispatcher;
    private volatile boolean running;

    public UnoServer(int port) throws IOException {
        if (port < MIN_PORT || port > MAX_PORT) {
            throw new IllegalArgumentException("Port must be between 1 and 65535");
        }
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        dispatcher = new CommandDispatcherImpl();
        ServerMessageGateway.setSender(this);
    }

    public void start() {
        Thread serverThread = new Thread(this);
        serverThread.start();
    }

    @Override
    public void run() {
        running = true;
        try {
            while (running) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    try {
                        if (key.isAcceptable()) acceptClient();
                        if (key.isValid() && key.isReadable()) readClient(key);
                        if (key.isValid() && key.isWritable()) writeClient(key);
                    } catch (IOException | CancelledKeyException exception) {
                        if (!(key.channel() instanceof SocketChannel)) {
                            throw exception;
                        }
                        disconnect(key);
                    }
                }
            }
        } catch (IOException | ClosedSelectorException exception) {
            if (running) {
                throw new IllegalStateException("UNO server stopped unexpectedly", exception);
            }
        } finally {
            close();
        }
    }

    private void acceptClient() throws IOException {
        SocketChannel channel = serverChannel.accept();
        if (channel == null) return;
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ, new ClientConnection());
    }

    private void readClient(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ClientConnection connection = (ClientConnection) key.attachment();
        int read = channel.read(connection.readBuffer);
        if (read == -1) {
            disconnect(key);
            return;
        }
        connection.readBuffer.flip();
        connection.input.append(StandardCharsets.UTF_8.decode(connection.readBuffer));
        connection.readBuffer.clear();

        int newline;
        while ((newline = connection.input.indexOf("\n")) >= 0) {
            String command = connection.input.substring(0, newline).trim();
            connection.input.delete(0, newline + 1);
            if (!command.isEmpty()) {
                queue(key, dispatcher.dispatch(command, channel));
            }
        }
    }

    private void writeClient(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ClientConnection connection = (ClientConnection) key.attachment();
        while (true) {
            ByteBuffer message;
            synchronized (connection.output) {
                message = connection.output.peek();
            }
            if (message == null) {
                key.interestOps(SelectionKey.OP_READ);
                return;
            }
            channel.write(message);
            if (message.hasRemaining()) return;
            synchronized (connection.output) {
                connection.output.remove();
            }
        }
    }

    private void queue(SelectionKey key, String message) {
        ClientConnection connection = (ClientConnection) key.attachment();
        synchronized (connection.output) {
            connection.output.add(StandardCharsets.UTF_8.encode((message == null ? "" : message)
                    + System.lineSeparator()));
        }
        key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
    }

    @Override
    public void send(SocketChannel channel, String message) {
        SelectionKey key = channel.keyFor(selector);
        if (key == null || !key.isValid()) {
            return;
        }
        queue(key, message);
        selector.wakeup();
    }

    private void disconnect(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        UserManagerImpl users = UserManagerImpl.getInstance();
        try {
            Player player;
            try {
                player = users.getPlayer(channel);
            } catch (UnoUserException exception) {
                // Connections without a session or game player need only socket cleanup.
                return;
            }
            GameManagerImpl games = GameManagerImpl.getInstance();
            if (games.isInGame(player.getId())) {
                games.leaveGame(player);
            }
        } catch (UnoUserException | PlayerNotFoundException exception) {
            System.err.println("Could not remove disconnected player: " + exception.getMessage());
        } finally {
            users.logout(channel);
            key.cancel();
            try {
                channel.close();
            } catch (IOException exception) {
                System.err.println("Could not close client: " + exception.getMessage());
            }
        }
    }

    @Override
    public void close() {
        running = false;
        ServerMessageGateway.clearSender(this);
        selector.wakeup();
        try {
            serverChannel.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        try {
            selector.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        try {
            UserManagerImpl.getInstance().save();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static final class ClientConnection {
        private final ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        private final StringBuilder input = new StringBuilder();
        private final Queue<ByteBuffer> output = new ArrayDeque<>();
    }
}
