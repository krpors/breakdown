package nl.rivium.breakdown.core;

import nl.rivium.breakdown.core.jms.JMSConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

@XmlAccessorType(XmlAccessType.FIELD)
public class TestCase extends GenericEntity<TestSuite, TestStep> {

    /**
     * Our logger.
     */
    private static Logger LOG = LoggerFactory.getLogger(TestCase.class);

    @XmlElement(name = "testStep")
    @XmlElementWrapper(name = "testSteps")
    private LinkedList<TestStep> testSteps = new LinkedList<>();

    /**
     * The actual constructed JMS queue connection by setUp(), if applicable.
     */
    @XmlTransient
    private Connection queueConnection;

    /**
     * The actual constructed JMS topic connection by setUp(), if applicable.
     */
    @XmlTransient
    private Connection topicConnection;

    public TestCase() {
    }

    /**
     * Creates a testcase with a name, and a parent TestSuite. The parent must not be null, will throw an
     * IllegalArgumentException if so.
     *
     * @param name   The name of the test suite.
     * @param parent The parent test suite.
     */
    public TestCase(String name, TestSuite parent) {
        super(name);
        setParent(parent);
        parent.getTestCases().add(this);
    }

    public List<TestStep> getTestSteps() {
        return testSteps;
    }

    public void setTestSteps(LinkedList<TestStep> testSteps) {
        this.testSteps = testSteps;
    }

    public Connection getQueueConnection() {
        return queueConnection;
    }

    public Connection getTopicConnection() {
        return topicConnection;
    }

    @Override
    public TestStep[] getChildren() {
        return testSteps.toArray(new TestStep[testSteps.size()]);
    }

    /**
     * Removes this testcase from the parent test suite.
     */
    @Override
    public void removeFromParent() {
        getParent().getTestCases().remove(this);
    }

    /**
     * Attempts to initialize the test case, see if things are working, like connections and such.
     */
    public void testSetup() throws BreakdownException {
        setUp();
        tearDown();
    }

    /**
     * Set up the test case before even beginning it.
     *
     * @throws Exception
     */
    private void setUp() throws BreakdownException {
        LOG.debug("TestCase '{}' setting up", getName());
        try {
            // TODO: fix this. Refer to an ID or name, and look that up in the project definition.
            // A connection doesn't necessarily need to exist. So we need to check on that too.
            JMSConnection jmsConnection = getParent().getParent().getJmsConnections().get(0);
            queueConnection = jmsConnection.createQueueConnection();
            topicConnection = jmsConnection.createTopicConnection();

            queueConnection.start();
            topicConnection.start();
        } catch (JMSException | NamingException ex) {
            String err = String.format("TestCase '%s': failed to setup", getName());
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

            LOG.info("TestCase '{}' -> '{}' input: {}", getName(), current.getName(), current.getInput());
            current.execute();
            LOG.info("TestCase '{}' -> '{}' output: {}", getName(), current.getName());
        }

        tearDown();
    }

    /**
     * Shut down resources, connections etc.
     */
    private void tearDown() throws BreakdownException {
        LOG.debug("TestCase '{}' tearing down", getName());
        try {
            queueConnection.stop();
            topicConnection.stop();

            queueConnection.close();
            topicConnection.close();
        } catch (JMSException ex) {
            String err = String.format("TestCase '{}': failed to tear down");
            LOG.error(err, ex);
            throw new BreakdownException(err, ex);
        }
    }

}
