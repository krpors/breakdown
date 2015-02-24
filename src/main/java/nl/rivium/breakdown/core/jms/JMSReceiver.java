package nl.rivium.breakdown.core.jms;

import nl.rivium.breakdown.core.*;

import javax.jms.*;

public class JMSReceiver extends TestStep<JMSReceiverInput, JMSReceiverOutput> {

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
     * @param previous  The previous test step. Useful to check the output of the previous step.
     * @throws AssertionException
     * @throws BreakdownException
     */
    @Override
    public void execute(TestStep previous) throws AssertionException, BreakdownException {
        try {
            Connection conn = getParent().getQueueConnection();
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
