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

    public static Project createProject() throws BreakdownException {
        JMSConnection c = new JMSConnection();
        c.setName("Localhost jms connection");
        c.setDescription("Bogus description");
        c.setContextFactory("com.tibco.tibjms.naming.TibjmsInitialContextFactory");
        c.setConnectionUrl("tcp://localhost:7222");
        c.setUsername("admin");
        c.setPassword(null);
        c.setQueueConnectionFactory("QueueConnectionFactory");
        c.setTopicConnectionFactory("TopicConnectionFactory");

        TestCase testCase = new TestCase("Testcase 1", "Sample testcase");

        // First test step:
        JMSRequestReply jrr = new JMSRequestReply("sample.queue sender", "Sends to the sample.queue. Reply on sample.topic");
        JMSSenderInput input = new JMSSenderInput();
        input.getProperties().put("Some Property", "Yarp!");
        input.getProperties().put("One", "1");
        input.setPayload("Payload!");

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

        TestSuite suite2 = new TestSuite("Suite 2", "Descirpiotasjd");

        Project p = new Project("Project 1", "Desc");
        p.getTestSuites().add(suite);
        p.getTestSuites().add(suite2);
        p.setAuthor("Me myself and I");
        p.getJmsConnections().add(c);

        p.setFilename("/home/whatevs/example/filename.xml");

        // set parents, for debuggin'
        jrr.setParent(testCase);
        ac.setParent(testCase);
        testCase.setParent(suite);
        suite.setParent(p);
        suite2.setParent(p);

        return p;
    }

    private static void jaxbStuff() throws JAXBException, BreakdownException {
        Project p = createProject();
        p.write(System.out);
    }

    private static void runProject() throws BreakdownException, AssertionException {
        Project p = createProject();
        p.execute();
    }


    public static void main(String[] args) throws Exception {
//        runProject();
        jaxbStuff();
    }
}
