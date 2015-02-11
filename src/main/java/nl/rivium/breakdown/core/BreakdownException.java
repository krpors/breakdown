package nl.rivium.breakdown.core;

/**
 * Generic exception class for Breakdown. Wraps other exceptions.
 */
public class BreakdownException extends Exception {

    public BreakdownException(String message) {
        super(message);
    }

    public BreakdownException(String message, Throwable cause) {
        super(message, cause);
    }
}
