package bg.sofia.uni.fmi.mjt.exception;

public class NotAColourChangeCardException extends RuntimeException {
    public NotAColourChangeCardException(String message) {
        super(message);
    }

    public NotAColourChangeCardException(String message, Throwable t) {
        super(message, t);
    }
}
