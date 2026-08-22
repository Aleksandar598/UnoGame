package bg.sofia.uni.fmi.mjt.server.exception;

public class MissingArgumentsException extends Exception {
    public MissingArgumentsException(String message) {
        super(message);
    }

    public MissingArgumentsException(String message, Throwable cause) {
        super(message, cause);
    }

}
