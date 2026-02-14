package bg.sofia.uni.fmi.mjt.exception;

public class NotAColourChangeCardException extends Exception {
    public NotAColourChangeCardException(String message) {
        super(message);
    }

    public NotAColourChangeCardException(String message, Throwable t) {
        super(message, t);
    }
}
