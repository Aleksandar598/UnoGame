package bg.sofia.uni.fmi.mjt.server.exception;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;

public class ExceptionLogger {

    private static final String FILE_PATH = "ServerExceptionLog.txt";
    private static final String GAME_FILE = "GameExceptionLog.txt";

    public static void logException(Exception e) throws IOException {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(FILE_PATH, true)))) {
            out.println("[" + LocalTime.now() + "] " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace(out);
            out.println();
        }
    }

    public static void logGameException(Exception e) throws IOException {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(GAME_FILE, true)))) {
            out.println("[" + LocalTime.now() + "] " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace(out);
            out.println();
        }
    }

    private ExceptionLogger() {

    }
}
