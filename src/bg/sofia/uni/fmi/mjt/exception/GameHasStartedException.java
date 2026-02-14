package bg.sofia.uni.fmi.mjt.exception;

public class GameHasStartedException extends Exception {

    public GameHasStartedException(String message) {
        super(message);
    }

    public GameHasStartedException(String message, Throwable t) {
        super(message, t);
    }
}
