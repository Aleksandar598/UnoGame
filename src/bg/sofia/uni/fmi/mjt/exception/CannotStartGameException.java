package bg.sofia.uni.fmi.mjt.exception;

public class CannotStartGameException extends Exception {
    public CannotStartGameException(String message) {
        super(message);
    }

    public CannotStartGameException(String message, Throwable t) {
        super(message, t);
    }
}
