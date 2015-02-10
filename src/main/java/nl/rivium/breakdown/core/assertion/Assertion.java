package nl.rivium.breakdown.core.assertion;

import nl.rivium.breakdown.core.AssertionException;
import nl.rivium.breakdown.core.TestCase;
import nl.rivium.breakdown.core.TestStep;

public interface Assertion {
    /**
     * Executes the assertion.
     *
     * @param testCase The test case where the test step resides in.
     * @param testStep The test step where assertions should be applied on.
     * @throws AssertionException When an assertion has failed.
     */
    public abstract void executeAssertion(TestCase testCase, TestStep testStep) throws AssertionException;
}
