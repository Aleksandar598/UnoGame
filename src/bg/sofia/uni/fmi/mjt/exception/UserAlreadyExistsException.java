package bg.sofia.uni.fmi.mjt.exception;

public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(String message, Throwable t) {
        super(message, t);
    }
}
