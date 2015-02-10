package nl.rivium.breakdown.core;

public class AssertionException extends Exception {

    private TestCase testCase;
    private TestStep testStep;
    private Object expected;
    private Object actual;

    public AssertionException(TestCase testCase, TestStep testStep, Object expected, Object actual) {
        this(testCase, testStep, expected, actual, "Assertion failed");
    }

    public AssertionException(TestCase testCase, TestStep testStep, Object expected, Object actual, String message) {
        super(message);
        this.expected = expected;
        this.actual = actual;
        this.testCase = testCase;
        this.testStep = testStep;
    }

    @Override
    public String getMessage() {
        return String.format("Test case '%s' -> '%s': expected '%s', got '%s'", testCase.getName(), testStep.getName(), expected, actual);
    }
}
