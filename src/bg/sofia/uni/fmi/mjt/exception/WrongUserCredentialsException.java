package bg.sofia.uni.fmi.mjt.exception;

public class WrongUserCredentialsException extends Exception {
    public WrongUserCredentialsException(String message) {
        super(message);
    }

    public WrongUserCredentialsException(String message, Throwable t) {
        super(message, t);
    }
}
