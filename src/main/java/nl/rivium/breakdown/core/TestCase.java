package nl.rivium.breakdown.core;


import javax.jms.Connection;
import java.util.ArrayList;
import java.util.List;

public class TestCase extends GenericEntity {

    private List<TestStep> testSteps = new ArrayList<>();

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

    public void setTestSteps(List<TestStep> testSteps) {
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
     * @throws Exception
     */
    private void setUp() throws Exception {
        queueConnection = jmsConnection.createQueueConnection();
        topicConnection = jmsConnection.createTopicConnection();
    }

    /**
     * Runs the testcase.
     */
    public void execute() throws Exception {
        setUp();
        for(TestStep step : testSteps) {
            System.out.printf("Step input: %s\n", step.getInput());
            step.execute();
            System.out.printf("Step output: %s\n", step.getOutput());
        }
        tearDown();
    }

    /**
     * Shut down resources, connections etc.
     */
    private void tearDown() throws Exception {
        queueConnection.close();
        topicConnection.close();
    }
}
