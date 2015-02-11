package nl.rivium.breakdown.core;

import nl.rivium.breakdown.core.jms.JMSConnection;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the TestCase class.
 */
public class TestTestCase {

    /**
     * Derp. This test requires a working local EMS. We should probably mock this.
     */
    @Test
    public void testSetup() {
        TestCase tc = new TestCase();
        JMSConnection c = new JMSConnection();
        c.setContextFactory("com.tibco.tibjms.naming.TibjmsInitialContextFactory");
        c.setConnectionUrl("tcp://localhost:7222");
        c.setUsername("admin");
        c.setPassword(null);
        c.setQueueConnectionFactory("QueueConnectionFactory");
        c.setTopicConnectionFactory("TopicConnectionFactory");
        tc.setJmsConnection(c);
        try {
            tc.testSetup();
        } catch (BreakdownException e) {
            Assert.fail("Did not expect exception");
        }
    }
}
