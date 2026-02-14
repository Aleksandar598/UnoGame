package bg.sofia.uni.fmi.mjt.exception;

public class UnoUserException extends Exception {
    public UnoUserException(String message) {
        super(message);
    }

    public UnoUserException(String message, Exception e) {
        super(message, e);
    }

}
