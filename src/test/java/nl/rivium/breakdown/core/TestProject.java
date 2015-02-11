package nl.rivium.breakdown.core;

import nl.rivium.breakdown.core.assertion.AssertionCollection;
import nl.rivium.breakdown.core.jms.JMSRequestReply;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.List;

/**
 * Tests project stuff.
 */
public class TestProject {

    @Test
    public void unmarshalling() {
        InputStream stream = TestProject.class.getResourceAsStream("/project.xml");
        Assert.assertNotNull(stream);

        try {
            Project p = Project.read(stream);
            Assert.assertEquals("Me myself and I", p.getAuthor());

            List<TestSuite> suites = p.getTestSuites();
            Assert.assertEquals(1, suites.size());

            List<TestCase> testCases = suites.get(0).getTestCases();
            Assert.assertEquals(1, testCases.size());

            List<TestStep> testSteps = testCases.get(0).getTestSteps();
            Assert.assertEquals(2, testSteps.size());

            TestStep one = testSteps.get(0);
            TestStep two = testSteps.get(1);

            Assert.assertTrue(one instanceof JMSRequestReply);
            Assert.assertTrue(two instanceof AssertionCollection);

            if (one instanceof JMSRequestReply) {
                JMSRequestReply jmsRequestReply = (JMSRequestReply) one;
                Assert.assertEquals("sample.queue sender", jmsRequestReply.getName());
                Assert.assertEquals("Payload!", jmsRequestReply.getInput().getPayload());
            }
        } catch (JAXBException | BreakdownException e) {
            Assert.fail(e.getMessage());
        }
    }
}
