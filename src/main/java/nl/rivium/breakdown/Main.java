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
import java.util.ArrayDeque;
import java.util.Queue;

public class Main {

    private static Logger LOG = LoggerFactory.getLogger(Main.class);

    public static Project createProject() throws BreakdownException {
        JMSConnection conn1 = new JMSConnection();
        conn1.setName("Localhost jms connection");
        conn1.setDescription("Bogus description");
        conn1.setContextFactory("com.tibco.tibjms.naming.TibjmsInitialContextFactory");
        conn1.setConnectionUrl("tcp://localhost:7222");
        conn1.setUsername("admin");
        conn1.setPassword(null);
        conn1.setQueueConnectionFactory("QueueConnectionFactory");
        conn1.setTopicConnectionFactory("TopicConnectionFactory");

        JMSConnection conn2 = new JMSConnection();
        conn2.setName("Other connection");
        conn2.setDescription("This is another connection.");
        conn2.setContextFactory("com.tibco.tibjms.naming.TibjmsInitialContextFactory");
        conn2.setConnectionUrl("tcp://localhost:7222");
        conn2.setUsername("admin");
        conn2.setPassword("nimda");
        conn2.setQueueConnectionFactory("QueueConnectionFactory");
        conn2.setTopicConnectionFactory("TopicConnectionFactory");

        TestCase testCase = new TestCase("Testcase 1", "Sample testcase");

        // First test step:
        JMSRequestReply jrr = new JMSRequestReply("sample.queue sender", "Sends to the sample.queue. Reply on sample.topic");
        jrr.setJmsConnectionName("Localhost jms connection"); // refers to the connection up top.
        JMSSenderInput input = new JMSSenderInput();
        input.getProperties().put("Some Property", "Yarp!");
        input.getProperties().put("One", "1");
        input.getProperties().put("Two", "2");
        input.getProperties().put("Three", "3");
        input.getProperties().put("PRCorrelationID", "Hi thar");
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
        p.getJmsConnections().add(conn1);
        p.getJmsConnections().add(conn2);

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

    private static void dispose(GenericEntity entity) {
        Queue<GenericEntity> q = new ArrayDeque<>();
        q.add(entity);
        while(q.peek() != null) {
            GenericEntity ge = q.remove();
            System.out.println("Got " + ge);
            for (GenericEntity child : ge.getChildren()) {
                q.add(child);
            }
        }
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
