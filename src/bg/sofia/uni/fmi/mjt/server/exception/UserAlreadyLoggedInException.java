package bg.sofia.uni.fmi.mjt.server.exception;

public class UserAlreadyLoggedInException extends Exception {
    public UserAlreadyLoggedInException(String message) {
        super(message);
    }
}
