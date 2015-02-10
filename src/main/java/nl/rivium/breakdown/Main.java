package nl.rivium.breakdown;


import ch.qos.logback.classic.BasicConfigurator;
import nl.rivium.breakdown.core.AssertionException;
import nl.rivium.breakdown.core.DestinationType;
import nl.rivium.breakdown.core.TestCase;
import nl.rivium.breakdown.core.assertion.AssertionCollection;
import nl.rivium.breakdown.core.assertion.StringAssertion;
import nl.rivium.breakdown.core.jms.JMSConnection;
import nl.rivium.breakdown.core.jms.JMSDestination;
import nl.rivium.breakdown.core.jms.JMSRequestReply;
import nl.rivium.breakdown.core.jms.JMSSenderInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    
    private static Logger LOG = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) throws Exception {
        JMSConnection c = new JMSConnection();
        c.setContextFactory("com.tibco.tibjms.naming.TibjmsInitialContextFactory");
        c.setConnectionUrl("tcp://localhost:7222");
        c.setUsername("admin");
        c.setPassword("");
        c.setQueueConnectionFactory("QueueConnectionFactory");
        c.setTopicConnectionFactory("TopicConnectionFactory");

        TestCase testCase = new TestCase("Hai", "Description");
        testCase.setJmsConnection(c);

        // First test step:
        JMSRequestReply jrr = new JMSRequestReply("sample.queue sender", "Sends to the sample.queue. Reply on sample.topic");
        JMSSenderInput input = new JMSSenderInput();
        input.setPayload("Payload!");
        input.getProperties().put("One", "1");

        jrr.setInput(input);
        jrr.setRequestDestination(new JMSDestination(DestinationType.QUEUE, "sample.queue"));
        jrr.setReplyDestination(new JMSDestination(DestinationType.TOPIC, "sample.topic"));

        // Second test step:
        StringAssertion sa = new StringAssertion("Some response");

        AssertionCollection ac = new AssertionCollection("Bunch of assertions", "String checks");
        ac.getAssertionList().add(sa);

        testCase.getTestSteps().add(jrr);
        testCase.getTestSteps().add(ac);

        try {
            testCase.execute();
        } catch (AssertionException ex) {
            LOG.error("Assertion failed: {}", ex.getMessage());
        }
    }
}
