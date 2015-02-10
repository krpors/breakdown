package nl.rivium.breakdown.core.assertion;

import nl.rivium.breakdown.core.AssertionException;
import nl.rivium.breakdown.core.BreakdownException;
import nl.rivium.breakdown.core.TestCase;
import nl.rivium.breakdown.core.TestStep;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of assertions, to execute after a test step.
 */
public class AssertionCollection extends TestStep {
    
    private List<Assertion> assertionList = new ArrayList<>();
    
    public AssertionCollection(String name, String description) {
        super(name, description);
    }

    public List<Assertion> getAssertionList() {
        return assertionList;
    }

    public void setAssertionList(List<Assertion> assertionList) {
        this.assertionList = assertionList;
    }

    @Override
    public void execute(TestCase testCase, TestStep previous) throws AssertionException, BreakdownException {
        for (Assertion ass : assertionList) {
            ass.executeAssertion(testCase, previous);
        }
    }
}
