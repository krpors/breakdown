package nl.rivium.breakdown.core.assertion;

import nl.rivium.breakdown.core.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.ArrayList;
import java.util.List;

/**
 * Collection of assertions, to execute after a test step.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class AssertionCollection extends TestStep {

    private List<Assertion> assertionList = new ArrayList<>();

    public AssertionCollection() {
    }

    public AssertionCollection(String name, TestCase parent) {
        super(name);
        setParent(parent);
        parent.getTestSteps().add(this);
    }

    public List<Assertion> getAssertionList() {
        return assertionList;
    }

    public void setAssertionList(List<Assertion> assertionList) {
        this.assertionList = assertionList;
    }

    @Override
    public void execute(TestStep previous) throws AssertionException, BreakdownException {
        for (Assertion ass : assertionList) {
            ass.executeAssertion(previous);
        }
    }
}
