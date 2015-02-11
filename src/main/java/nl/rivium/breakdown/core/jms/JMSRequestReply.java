package nl.rivium.breakdown.core.jms;

import nl.rivium.breakdown.core.*;

import javax.jms.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class JMSRequestReply extends TestStep<JMSSenderInput, JMSReceiverOutput> {

    private long timeout = 10000;
    private JMSDestination requestDestination;
    private JMSDestination replyDestination;

    public JMSRequestReply() {
    }

    public JMSRequestReply(String name, String description) {
        super(name, description);
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

    @Override
    public void execute(Project project, TestSuite suite, TestCase testCase, TestStep previous) throws AssertionException, BreakdownException {
        // TODO: in EMS, using this connection works for queues and topics both. Why? What!
        try {
            Connection connection = testCase.getQueueConnection();
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
                throw new AssertionException(testCase, this, TextMessage.class.getName(), null, "No message received");
            }
        } catch (JMSException ex) {
            throw new BreakdownException("Failed to execute test step", ex);
        }
    }
}
