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
        Project p = new Project("Project 1");

        JMSConnection conn1 = new JMSConnection("Localhost jms connection", p);
        conn1.setDescription("Bogus description");
        conn1.setContextFactory("com.tibco.tibjms.naming.TibjmsInitialContextFactory");
        conn1.setConnectionUrl("tcp://localhost:7222");
        conn1.setUsername("admin");
        conn1.setPassword(null);
        conn1.setQueueConnectionFactory("QueueConnectionFactory");
        conn1.setTopicConnectionFactory("TopicConnectionFactory");

        JMSConnection conn2 = new JMSConnection("Other connection", p);
        conn2.setDescription("This is another connection.");
        conn2.setContextFactory("com.tibco.tibjms.naming.TibjmsInitialContextFactory");
        conn2.setConnectionUrl("tcp://localhost:7222");
        conn2.setUsername("admin");
        conn2.setPassword("nimda");
        conn2.setQueueConnectionFactory("QueueConnectionFactory");
        conn2.setTopicConnectionFactory("TopicConnectionFactory");

        TestSuite suite = new TestSuite("Suite 1", p);
        TestSuite suite2 = new TestSuite("Suite 2", p);

        TestCase testCase = new TestCase("Testcase 1", suite);

        // First test step:
        JMSRequestReply jrr = new JMSRequestReply("sample.queue sender", testCase);

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

        AssertionCollection ac = new AssertionCollection("Bunch of assertions", testCase);
        ac.getAssertionList().add(sa);

        p.setAuthor("Me myself and I");

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
