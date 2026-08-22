package bg.sofia.uni.fmi.mjt.server.network;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class ServerMessageGatewayTest {
    private static final String MESSAGE = "Game has started";

    private MessageSender messageSender;
    private SocketChannel socket;

    @BeforeEach
    void setUp() {
        ServerMessageGateway.setSender(null);
        messageSender = Mockito.mock(MessageSender.class);
        socket = Mockito.mock(SocketChannel.class);
    }

    @AfterEach
    void tearDown() {
        ServerMessageGateway.setSender(null);
    }

    @Test
    void testSendDelegatesToConfiguredMessageSender() {
        ServerMessageGateway.setSender(messageSender);

        ServerMessageGateway.send(socket, MESSAGE);

        verify(messageSender).send(socket, MESSAGE);
    }

    @Test
    void testSendIgnoresNullSocketOrMessage() {
        ServerMessageGateway.setSender(messageSender);

        ServerMessageGateway.send(null, MESSAGE);
        ServerMessageGateway.send(socket, null);

        verifyNoInteractions(messageSender);
    }

    @Test
    void testClearSenderStopsForwardingForSameSender() {
        ServerMessageGateway.setSender(messageSender);
        ServerMessageGateway.clearSender(messageSender);

        ServerMessageGateway.send(socket, MESSAGE);

        verifyNoInteractions(messageSender);
    }

    @Test
    void testClearSenderDoesNotRemoveDifferentSender() {
        MessageSender differentSender = Mockito.mock(MessageSender.class);
        ServerMessageGateway.setSender(messageSender);
        ServerMessageGateway.clearSender(differentSender);

        ServerMessageGateway.send(socket, MESSAGE);

        verify(messageSender).send(socket, MESSAGE);
        verifyNoInteractions(differentSender);
    }
}
