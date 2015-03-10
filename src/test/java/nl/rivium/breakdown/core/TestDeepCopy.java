package nl.rivium.breakdown.core;

import nl.rivium.breakdown.core.jms.DestinationType;
import nl.rivium.breakdown.core.jms.JMSConnection;
import nl.rivium.breakdown.core.jms.JMSDestination;
import nl.rivium.breakdown.core.jms.JMSRequestReply;
import org.apache.commons.lang.SerializationUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Testing deep copy stuff.
 */
public class TestDeepCopy {

    @Test
    public void copy() {
        Project p = new Project();
        p.setName("Project");
        p.setAuthor("Me");
        p.setDescription("Description");

        JMSConnection conn = new JMSConnection("Connection 1", p);
        conn.setConnectionUrl("tcp://localhost:7222");

        TestSuite suite = new TestSuite("TestSuite 1", p);
        suite.setDescription("Test suite description");

        TestCase testCase = new TestCase("Test Case 1", suite);
        testCase.setDescription("Desc desc");

        JMSRequestReply requestReply = new JMSRequestReply("JRR", testCase);
        requestReply.setTimeout(31337);
        requestReply.setJmsConnectionName("Connection 1");
        requestReply.setReplyDestination(new JMSDestination(DestinationType.QUEUE, "Sample queue"));

        // Clone the object using Serialization utils, and test whether properties are copied.
        Object o = SerializationUtils.clone(p);
        assertTrue(o instanceof Project);

        Project pClone = (Project) o;
        TestSuite sClone = pClone.getTestSuites().get(0);
        TestCase cClone = sClone.getTestCases().get(0);
        JMSRequestReply jrrClone = (JMSRequestReply) cClone.getTestSteps().get(0);

        assertEquals("Me", pClone.getAuthor());
        assertEquals("Project", pClone.getName());
        assertEquals("Description", pClone.getDescription());

        assertEquals("TestSuite 1", sClone.getName());
        assertEquals("Test Case 1", cClone.getName());
        assertEquals("JRR", jrrClone.getName());
        assertEquals(31337, jrrClone.getTimeout());
        assertEquals("Sample queue", jrrClone.getReplyDestination().getName());
        assertEquals(DestinationType.QUEUE, jrrClone.getReplyDestination().getType());
    }
}
