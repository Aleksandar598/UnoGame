package bg.sofia.uni.fmi.mjt.server.network;

import bg.sofia.uni.fmi.mjt.server.UnoServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import bg.sofia.uni.fmi.mjt.server.game.GameManagerImpl;
import bg.sofia.uni.fmi.mjt.server.user.UserManagerImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testDisconnectRemovesPlayerAndKeepsOtherClientsWorking(boolean reset) throws Exception {
        int port = availablePort();
        String username = "disconnect-" + UUID.randomUUID();
        String gameId = "game-" + UUID.randomUUID();
        UserManagerImpl.getInstance().register(username, "password");
        GameManagerImpl games = GameManagerImpl.getInstance();
        games.createGame(gameId, username, 4);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        UnoServer server = new UnoServer(port);
        Thread thread = new Thread(server);
        thread.setUncaughtExceptionHandler((ignored, exception) -> failure.set(exception));
        thread.start();
        try (Socket survivor = new Socket("localhost", port)) {
            survivor.setSoTimeout(3000);
            BufferedReader survivorReader = reader(survivor);
            PrintWriter survivorWriter = writer(survivor);
            survivorWriter.println(PROTECTED_COMMAND);
            assertEquals(EXPECTED_NOT_LOGGED_IN_RESPONSE, survivorReader.readLine());

            try (Socket departing = new Socket("localhost", port)) {
                departing.setSoTimeout(3000);
                BufferedReader departingReader = reader(departing);
                PrintWriter departingWriter = writer(departing);
                departingWriter.println("login --username=" + username + " --password=password");
                assertEquals("Login Successful", departingReader.readLine());
                departingWriter.println("join --game-id=" + gameId);
                assertEquals(username + " Successfully joined game", departingReader.readLine());
                assertEquals("Successfully joined game", departingReader.readLine());
                if (reset) departing.setSoLinger(true, 0);
            }

            long deadline = System.nanoTime() + 3_000_000_000L;
            while (games.listGames().stream().anyMatch(game -> game.gameId().equals(gameId))
                    && thread.isAlive() && System.nanoTime() < deadline) {
                Thread.sleep(10);
            }
            assertFalse(games.listGames().stream().anyMatch(game -> game.gameId().equals(gameId)),
                    "Disconnected player should be removed, deleting the now-empty game");
            survivorWriter.println(PROTECTED_COMMAND);
            assertEquals(EXPECTED_NOT_LOGGED_IN_RESPONSE, survivorReader.readLine());
            survivorWriter.println("login --username=" + username + " --password=password");
            assertEquals("Login Successful", survivorReader.readLine(), "Disconnected session must be released");
            survivorWriter.println("logout");
            assertEquals("Logout Successful", survivorReader.readLine());
        } finally {
            server.close();
            thread.join(3000);
        }
        assertFalse(thread.isAlive());
        assertNull(failure.get(), "The server thread must not crash");
    }

    private BufferedReader reader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
    }

    private PrintWriter writer(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
    }

}
