package bg.sofia.uni.fmi.mjt.server;

import java.io.IOException;

public final class StartServer {
    private static final int DEFAULT_PORT = 7777;

    private StartServer() {
    }

    public static void main(String[] args) throws IOException {
        int port = args.length == 0 ? DEFAULT_PORT : Integer.parseInt(args[0]);

        try (UnoServer server = new UnoServer(port)) {
            System.out.println("UNO server started on port " + port);
            server.run();
        }
    }
}
