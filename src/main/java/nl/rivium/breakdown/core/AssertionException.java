package nl.rivium.breakdown.core;


public class AssertionException extends Exception {

    private TestCase testCase;
    private TestStep testStep;
    private Object expected;
    private Object actual;

    public AssertionException() {
    }

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

    /**
     * @return
     */
    public TestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
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
        return String.format("Test case '%s' -> '%s': expected '%s', got '%s'", testCase.getName(), testStep.getName(), expected, actual);
    }
}
