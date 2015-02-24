package nl.rivium.breakdown.core.jms;

import nl.rivium.breakdown.core.*;

import javax.jms.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * A simple JMS request reply test step. Sends a JMS Text Message to a destination, with some payload and optionally
 * some extra properties. A response is expected on an reply destination within the configured amount of time.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JMSRequestReply extends TestStep<JMSSenderInput, JMSReceiverOutput> {

    /**
     * Default timeout to wait for a response.
     */
    private long timeout = 10000;

    /**
     * The request destination (to send data to).
     */
    private JMSDestination requestDestination;

    /**
     * The reply destination (to expect data from).
     */
    private JMSDestination replyDestination;

    /**
     * The JMS connection ID. The connections are defined project wide in the Project class.
     * This class refers to a connection by this ID (which is the name of the JMSConnection).
     * The findConnection() can be used to track it down.
     */
    private String jmsConnectionName;

    public JMSRequestReply() {
    }

    /**
     * Creates a new JMSRequestReply test step.
     * @param name The name of the step.
     * @param parent The TestCase parent.
     */
    public JMSRequestReply(String name, TestCase parent) {
        super(name);
        setParent(parent);
    }

    public JMSDestination getRequestDestination() {
        return requestDestination;
    }

    public void setRequestDestination(JMSDestination requestDestination) {
        this.requestDestination = requestDestination;
    }

    public JMSDestination getReplyDestination() {
        return replyDestination;
    }

    public void setReplyDestination(JMSDestination replyDestination) {
        this.replyDestination = replyDestination;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public String getJmsConnectionName() {
        return jmsConnectionName;
    }

    public void setJmsConnectionName(String jmsConnectionName) {
        this.jmsConnectionName = jmsConnectionName;
    }

    /**
     * Checks the top most parent Project instance, and find the connection with the given name. Null will be returned
     * if the project did not contain a connection with that name.
     *
     * @return The JMSConnection defined in the Project instance with that name, or null if nothing can be found.
     */
    public JMSConnection findConnection() {
        if (getJmsConnectionName() == null) {
            return null;
        }

        Project parentProject = getParent().getParent().getParent();
        return parentProject.findJMSConnectionByName(getJmsConnectionName());
    }

    @Override
    public void execute(TestStep previous) throws AssertionException, BreakdownException {
        // TODO: in EMS, using this connection works for queues and topics both. Why? What!
        try {
            Connection connection = getParent().getQueueConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destRequest = requestDestination.createDestination(session);
            Destination destReply = replyDestination.createDestination(session);

            // Create producer and consumer:
            MessageProducer producer = session.createProducer(destRequest);
            MessageConsumer consumer = session.createConsumer(destReply);
            TextMessage tm = session.createTextMessage();
            tm.setText(getInput().getPayload());
            producer.send(tm);

            // Receive message, using timeout
            Message m = consumer.receive(getTimeout());
            if (m instanceof TextMessage) {
                TextMessage replyMessage = (TextMessage) m;

                JMSReceiverOutput output = new JMSReceiverOutput();
                output.setPayload(replyMessage.getText());

                setOutput(output);
            } else {
                throw new AssertionException(this, TextMessage.class.getName(), null, "No message received");
            }
        } catch (JMSException ex) {
            throw new BreakdownException("Failed to execute test step", ex);
        }
    }
}
