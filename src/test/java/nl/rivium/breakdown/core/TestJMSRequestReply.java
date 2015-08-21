package nl.rivium.breakdown.core;

import nl.rivium.breakdown.core.assertion.PayloadAssertion;
import nl.rivium.breakdown.core.jms.*;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.List;

/**
 * Tests project stuff.
 */
public class TestJMSRequestReply {

    @Test
    public void lol() {
        Project p = new Project("");

        JMSConnection connection1 = new JMSConnection("tibems", p);
        connection1.setDescription("Bogus description");
        connection1.setContextFactory("com.tibco.tibjms.naming.TibjmsInitialContextFactory");
        connection1.setConnectionUrl("tcp://localhost:7222");
        connection1.setUsername("admin");
        connection1.setPassword("admin");
        connection1.setQueueConnectionFactory("QueueConnectionFactory");
        connection1.setTopicConnectionFactory("TopicConnectionFactory");

        TestSuite ts = new TestSuite("ASD", p);
        TestCase tc = new TestCase("Blah", ts);
        JMSRequestReply rr = new JMSRequestReply("RR", tc);
        rr.setJmsConnectionName("tibems");
        rr.setTimeout(5000);
        rr.setRequestDestination(new JMSDestination(DestinationType.QUEUE, "sample.queue"));
        rr.setReplyDestination(new JMSDestination(DestinationType.TOPIC, "sample.topic"));
        JMSSenderInput input = new JMSSenderInput();
        input.setPayload("hai");
        rr.setInput(input);

        rr.getPayloadAssertions().add(new PayloadAssertion("Some response"));


        try {
            tc.execute();
        } catch (AssertionException e) {

        } catch (BreakdownException e) {
            e.printStackTrace();
        }
    }
}
