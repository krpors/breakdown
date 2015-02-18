package nl.rivium.breakdown.core;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class TestSuite extends GenericEntity<Project, TestCase> {

    @XmlElement(name = "testCase")
    @XmlElementWrapper(name = "testCases")
    private List<TestCase> testCases = new ArrayList<>();

    public TestSuite() {
    }

    public TestSuite(String name, String description) {
        super(name, description);
    }

    public TestSuite(String name, Project parent) {
        super(name, "");
        setParent(parent);
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }

    @Override
    public TestCase[] getChildren() {
        return testCases.toArray(new TestCase[testCases.size()]);
    }

    /**
     * Executes the TestSuite and all the TestCases with TestSteps.
     */
    public void execute(Project project) throws BreakdownException, AssertionException {
        for (TestCase tc : testCases) {
            tc.execute(project, this);
        }
    }
}
