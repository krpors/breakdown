package nl.rivium.breakdown;


import nl.rivium.breakdown.core.*;
import nl.rivium.breakdown.core.assertion.AssertionCollection;
import nl.rivium.breakdown.core.assertion.StringAssertion;
import nl.rivium.breakdown.core.jms.JMSConnection;
import nl.rivium.breakdown.core.jms.JMSDestination;
import nl.rivium.breakdown.core.jms.JMSRequestReply;
import nl.rivium.breakdown.core.jms.JMSSenderInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;

public class Main {

    private static Logger LOG = LoggerFactory.getLogger(Main.class);

    private static Project createProject() throws BreakdownException {
        JMSConnection c = new JMSConnection();
        c.setContextFactory("com.tibco.tibjms.naming.TibjmsInitialContextFactory");
        c.setConnectionUrl("tcp://localhost:7222");
        c.setUsername("admin");
        c.setPassword(null);
        c.setQueueConnectionFactory("QueueConnectionFactory");
        c.setTopicConnectionFactory("TopicConnectionFactory");

        TestCase testCase = new TestCase("Testcase 1", "Sample testcase");
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

        TestSuite suite = new TestSuite("Suite 1", "Desc");
        suite.getTestCases().add(testCase);

        Project p = new Project("Project 1", "Desc");
        p.getTestSuites().add(suite);
        p.setAuthor("Me myself and I");

        return p;
    }

    private static void jaxbStuff() throws JAXBException, BreakdownException {
        Project p = createProject();
        p.write();
    }


    public static void main(String[] args) throws Exception {
        jaxbStuff();
    }
}
