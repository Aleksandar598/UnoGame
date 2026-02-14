package bg.sofia.uni.fmi.mjt.exception;

public class MaximumPlayerCountReached extends Exception {
    public MaximumPlayerCountReached(String message) {
        super(message);
    }

    public MaximumPlayerCountReached(String message, Throwable t) {
        super(message, t);
    }
}
