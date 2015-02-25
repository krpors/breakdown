package nl.rivium.breakdown.core;


public class AssertionException extends Exception {

   private Object expected;
    private Object actual;

    public AssertionException() {
    }

    public AssertionException(Object expected, Object actual) {
        this(expected, actual, "Assertion failed");
    }

    public AssertionException(Object expected, Object actual, String message) {
        super(message);
        this.expected = expected;
        this.actual = actual;
    }

    public Object getExpected() {
        return expected;
    }

    public void setExpected(Object expected) {
        this.expected = expected;
    }

    public Object getActual() {
        return actual;
    }

    public void setActual(Object actual) {
        this.actual = actual;
    }

    @Override
    public String getMessage() {
        return String.format("Expected '%s', got '%s'", expected, actual);
    }
}
