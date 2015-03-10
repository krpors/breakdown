package nl.rivium.breakdown.core.jms;

import nl.rivium.breakdown.core.AssertionException;
import nl.rivium.breakdown.core.BreakdownException;
import nl.rivium.breakdown.core.TestCase;
import nl.rivium.breakdown.core.TestStep;

import javax.jms.*;
import java.io.Serializable;

public class JMSReceiver extends TestStep<JMSReceiverInput> implements Serializable {

    private static final long serialVersionUID = -3327677613498175080L;

    public static final long TIMEOUT_DEFAULT = 10000;

    private String destination;
    private long timeout = TIMEOUT_DEFAULT;

    public JMSReceiver(String name, TestCase parent) {
        super(name);
        setParent(parent);
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

    /**
     * Execute this JMSReceiver test step
     *
     * @throws AssertionException
     * @throws BreakdownException
     */
    @Override
    public void execute() throws AssertionException, BreakdownException {
        try {
            Connection conn = getParent().getQueueConnection();
            Session s = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue q = s.createQueue(getDestination());
            MessageConsumer cons = s.createConsumer(q);
            Message m = cons.receive(getTimeout());
            if (m instanceof TextMessage) {
                TextMessage tm = (TextMessage) m;

                System.out.println(tm.getText());
            }

            s.close();
        } catch (JMSException ex) {
            throw new BreakdownException("Failed to execute test case", ex);
        }
    }
}
