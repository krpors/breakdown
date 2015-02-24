package nl.rivium.breakdown.core.assertion;

import nl.rivium.breakdown.core.AssertionException;
import nl.rivium.breakdown.core.TestCase;
import nl.rivium.breakdown.core.TestStep;

public abstract class Assertion {
    /**
     * Executes the assertion.
     *
     * @param testStep The test step where assertions should be applied on.
     * @throws AssertionException When an assertion has failed.
     */
    public abstract void executeAssertion(TestStep testStep) throws AssertionException;
}
