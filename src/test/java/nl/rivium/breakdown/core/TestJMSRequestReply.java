package nl.rivium.breakdown.core;

import nl.rivium.breakdown.core.assertion.PayloadAssertion;
import nl.rivium.breakdown.core.jms.*;
import org.junit.Test;

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


        tc.addResultListener(new ResultListener() {
            @Override
            public void resultAcquired(Result result) {
                System.out.println(result.getMessage());
                System.out.println(result.getTestStep());
            }
        });

        try {
            tc.execute();
        } catch (BreakdownException e) {
            e.printStackTrace();
        }
    }
}
