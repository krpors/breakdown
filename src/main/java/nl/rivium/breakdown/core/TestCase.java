package nl.rivium.breakdown.core;

import nl.rivium.breakdown.core.jms.JMSConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.naming.NamingException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class TestCase extends GenericEntity {

    /**
     * Our logger.
     */
    private static Logger LOG = LoggerFactory.getLogger(TestCase.class);

    private LinkedList<TestStep> testSteps = new LinkedList<>();

    private JMSConnection jmsConnection;

    /**
     * The actual constructed JMS connection by setUp().
     */
    private Connection queueConnection;

    private Connection topicConnection;

    public TestCase(String name, String description) {
        super(name, description);
    }


    public List<TestStep> getTestSteps() {
        return testSteps;
    }

    public void setTestSteps(LinkedList<TestStep> testSteps) {
        this.testSteps = testSteps;
    }

    public JMSConnection getJmsConnection() {
        return jmsConnection;
    }

    public void setJmsConnection(JMSConnection jmsConnection) {
        this.jmsConnection = jmsConnection;
    }

    public Connection getQueueConnection() {
        return queueConnection;
    }

    public Connection getTopicConnection() {
        return topicConnection;
    }

    /**
     * Set up the test case before even beginning it.
     *
     * @throws Exception
     */
    private void setUp() throws BreakdownException {
        LOG.debug("TestCase '{}' setting up", getName());
        try {
            queueConnection = jmsConnection.createQueueConnection();
            topicConnection = jmsConnection.createTopicConnection();

            queueConnection.start();
            topicConnection.start();
        } catch (JMSException | NamingException ex) {
            String err = String.format("TestCase '{}': failed to setup");
            LOG.error(err, ex);
            throw new BreakdownException(err, ex);
        }
    }

    /**
     * Runs the testcase.
     */
    public void execute() throws AssertionException, BreakdownException {
        setUp();

        ListIterator<TestStep> it = testSteps.listIterator();
        // We use an iterator here to get a possible previous test case when they are chained together. This is useful
        // for assertions so you can refer to the previous run step etc.
        while (it.hasNext()) {
            // First attempt to get the previous test step. It can be null if the iteration is at the very first 
            // test step.
            TestStep previous = null;
            if (it.hasPrevious()) {
                previous = testSteps.get(it.previousIndex());
            }

            TestStep current = it.next();

            System.out.printf("Step input: %s\n", current.getInput());
            current.execute(this, previous);
            System.out.printf("Step output: %s\n", current.getOutput());
        }

        tearDown();
    }

    /**
     * Shut down resources, connections etc.
     */
    private void tearDown() throws BreakdownException {
        try {
            queueConnection.stop();
            topicConnection.stop();

            queueConnection.close();
            topicConnection.close();
        } catch (JMSException ex) {
            throw new BreakdownException("Failed to tear down test case", ex);
        }
    }
}
