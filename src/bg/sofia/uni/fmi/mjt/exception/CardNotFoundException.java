package bg.sofia.uni.fmi.mjt.exception;

public class CardNotFoundException extends Exception {
    public CardNotFoundException(String message) {
        super(message);
    }

    public CardNotFoundException(String message, Throwable t) {
        super(message, t);
    }
}
