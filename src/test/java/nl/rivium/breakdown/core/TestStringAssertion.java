package nl.rivium.breakdown.core;

import nl.rivium.breakdown.core.assertion.AssertionCollection;
import nl.rivium.breakdown.core.assertion.StringAssertion;
import nl.rivium.breakdown.core.jms.JMSReceiverOutput;
import nl.rivium.breakdown.core.jms.JMSRequestReply;
import org.junit.Assert;
import org.junit.Test;

public class TestStringAssertion {

    @Test
    public void assertion() {
        Project p = new Project("Test project");
        TestSuite suite = new TestSuite("Test suite", p);
        TestCase testCase = new TestCase("Test case", suite);

        JMSRequestReply jrr = new JMSRequestReply("Req/rep", testCase);
        JMSReceiverOutput output = new JMSReceiverOutput();
        output.setPayload("This is some payload");
        jrr.setOutput(output);

        AssertionCollection step = new AssertionCollection("Assertions", testCase);
        step.getAssertionList().add(new StringAssertion("some payload"));

        try {
            step.execute(jrr);
        } catch (AssertionException | BreakdownException e) {
            Assert.fail("Did not expect an assertion at this point");
        }
    }
}
