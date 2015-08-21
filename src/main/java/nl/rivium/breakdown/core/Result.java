package nl.rivium.breakdown.core;

import nl.rivium.breakdown.core.TestStep;

/**
 * Result of running a test step.
 */
public class Result {
    private boolean success;
    private String message;
    private Exception exception;
    private TestStep testStep;

    public Result(TestStep step, boolean success) {
        this(step, success, "");
    }

    public Result(TestStep step, boolean success, String message) {
        this(step, success, message, null);
    }

    public Result(TestStep step, boolean success, String message, Exception ex) {
        this.testStep = step;
        this.success = success;
        this.message = message;
        this.exception = ex;
    }

    public TestStep getTestStep() {
        return testStep;
    }

    public void setTestStep(TestStep testStep) {
        this.testStep = testStep;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        String exmsg = getException() != null ? getException().getMessage() : "";
        return String.format("Result[success=%b, msg=%s, exception=%s]", isSuccess(), getMessage(), exmsg);
    }
}
