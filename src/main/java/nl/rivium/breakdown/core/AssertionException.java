package nl.rivium.breakdown.core;


public class AssertionException extends Exception {

    private TestStep testStep;
    private Object expected;
    private Object actual;

    public AssertionException() {
    }

    public AssertionException(TestStep testStep, Object expected, Object actual) {
        this(testStep, expected, actual, "Assertion failed");
    }

    public AssertionException(TestStep testStep, Object expected, Object actual, String message) {
        super(message);
        this.expected = expected;
        this.actual = actual;
        this.testStep = testStep;
    }

    public TestStep getTestStep() {
        return testStep;
    }

    public void setTestStep(TestStep testStep) {
        this.testStep = testStep;
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
        return String.format("Test step '%s': expected '%s', got '%s'", testStep.getName(), expected, actual);
    }
}
