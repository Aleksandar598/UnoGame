package bg.sofia.uni.fmi.mjt.exception;

public class PlayerNotFoundException extends Exception {
    public PlayerNotFoundException(String message) {
        super(message);
    }

    public PlayerNotFoundException(String message, Throwable t) {
        super(message, t);
    }
}
