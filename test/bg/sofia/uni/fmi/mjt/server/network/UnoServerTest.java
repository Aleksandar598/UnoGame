package bg.sofia.uni.fmi.mjt.server.network;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UnoServerTest {
    private static final int PORT_BELOW_MINIMUM = 0;
    private static final int PORT_ABOVE_MAXIMUM = 65_536;
    private static final String EXPECTED_INVALID_PORT_MESSAGE = "Port must be between 1 and 65535";
    private static final String PROTECTED_COMMAND = "list-games";
    private static final String EXPECTED_NOT_LOGGED_IN_RESPONSE = "You are not logged in";

    @Test
    void testRejectsPortBelowMinimum() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new UnoServer(PORT_BELOW_MINIMUM));

        assertEquals(EXPECTED_INVALID_PORT_MESSAGE, exception.getMessage());
    }

    @Test
    void testRejectsPortAboveMaximum() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new UnoServer(PORT_ABOVE_MAXIMUM));

        assertEquals(EXPECTED_INVALID_PORT_MESSAGE, exception.getMessage());
    }

    @Test
    void testRespondsToUnauthenticatedClientCommand() throws IOException {
        int port = availablePort();
        try (UnoServer server = new UnoServer(port)) {
            server.start();

            try (Socket socket = new Socket("localhost", port);
                 BufferedReader responseReader = new BufferedReader(
                         new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                 PrintWriter commandWriter = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)) {

                commandWriter.println(PROTECTED_COMMAND);

                assertEquals(EXPECTED_NOT_LOGGED_IN_RESPONSE, responseReader.readLine());
            }
        }
    }

    @Test
    void testCloseCanBeCalledMoreThanOnce() throws IOException {
        UnoServer server = new UnoServer(availablePort());

        assertDoesNotThrow(server::close);
        assertDoesNotThrow(server::close);
    }

    private int availablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

}
