package bg.sofia.uni.fmi.mjt.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public final class UnoClient {

    private static final int PORT = 7777;
    private static final int CONNECTION_TIMEOUT_MILLIS = 2_000;
    private static final int RECONNECT_DELAY_MILLIS = 2_000;

    private UnoClient() { }

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : PORT;

        while (!Thread.currentThread().isInterrupted()) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), CONNECTION_TIMEOUT_MILLIS);
                System.out.println("Connected to server!");
                runSession(socket);
                return;
            } catch (IOException exception) {
                System.out.println("Could not connect to server. Retrying in 2 seconds...");
                if (!waitBeforeRetry()) {
                    return;
                }
            }
        }
    }

    private static void runSession(Socket socket) throws IOException {
        try (BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
             BufferedReader server = new BufferedReader(new InputStreamReader(socket.getInputStream(),
                     StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)) {

            Thread.ofVirtual()
                    .name("uno-client-reader")
                    .start(() -> readResponses(server));

            String command;
            while ((command = keyboard.readLine()) != null && !command.equalsIgnoreCase("exit")) {
                out.println(command);
            }
        }
    }

    private static boolean waitBeforeRetry() {
        try {
            Thread.sleep(RECONNECT_DELAY_MILLIS);
            return true;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private static void readResponses(BufferedReader server) {
        try {
            String response;
            while ((response = server.readLine()) != null) {
                System.out.println(response);
            }
        } catch (IOException exception) {
            System.out.println("Disconnected from server");
        }
    }
}
