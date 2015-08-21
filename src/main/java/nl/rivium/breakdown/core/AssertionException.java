package nl.rivium.breakdown.core;

public class AssertionException extends Exception {

    private TestStep source;
    private Object expected;
    private Object actual;

    public AssertionException(TestStep source) {
        this.source = source;
    }

    public AssertionException(TestStep source, Object expected, Object actual) {
        this(source, expected, actual, "Assertion failed");
    }

    public AssertionException(TestStep source, Object expected, Object actual, String message) {
        super(message);
        this.source = source;
        this.expected = expected;
        this.actual = actual;
    }

    /**
     * Returns the teststep which failed.
     *
     * @return The failed teststep.
     */
    public TestStep getTestStep() {
        return source;
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
