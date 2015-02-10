package nl.rivium.breakdown.core.jms;

import nl.rivium.breakdown.core.AssertionException;
import nl.rivium.breakdown.core.BreakdownException;
import nl.rivium.breakdown.core.TestCase;
import nl.rivium.breakdown.core.TestStep;

import javax.jms.*;

public class JMSReceiver extends TestStep<JMSReceiverInput, JMSReceiverOutput> {

    public static final long TIMEOUT_DEFAULT = 10000;

    private String destination;
    private long timeout = TIMEOUT_DEFAULT;

    public JMSReceiver(String name, String description) {
        super(name, description);
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public void execute(TestCase testCase, TestStep previous) throws AssertionException, BreakdownException {
        try {
            Connection conn = testCase.getQueueConnection();
            Session s = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue q = s.createQueue(getDestination());
            MessageConsumer cons = s.createConsumer(q);
            Message m = cons.receive(getTimeout());
            if (m instanceof TextMessage) {
                TextMessage tm = (TextMessage) m;
                JMSReceiverOutput output = new JMSReceiverOutput();
                output.setPayload(tm.getText());
                // TODO set properties
                setOutput(output);

                System.out.println(tm.getText());
            }

            s.close();
        } catch (JMSException ex) {
            throw new BreakdownException("Failed to execute test case", ex);
        }
    }
}
