package nl.rivium.breakdown.core.jms;

import nl.rivium.breakdown.core.*;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

public class JMSSender extends TestStep<JMSSenderInput, JMSSenderOutput> {

    private String destination;
    private Map<String, String> properties = new HashMap<>();
    private TestCase testCase;

    public JMSSender(String name, String description) {
        super(name, description);
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public void execute(Project project, TestSuite suite, TestCase testCase, TestStep previous) throws AssertionException, BreakdownException {
        try {
            Connection c = testCase.getQueueConnection();
            Session session = c.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(getDestination());
            TextMessage msg = session.createTextMessage();
            msg.setText(getInput().getPayload());
            for (String k : getInput().getProperties().keySet()) {
                String v = getInput().getProperties().get(k);
                msg.setStringProperty(k, v);
            }
            MessageProducer producer = session.createProducer(queue);
            producer.send(msg);

            JMSSenderOutput output = new JMSSenderOutput();
            output.setJmsMessageId(msg.getJMSMessageID());
            output.setJmsTimestamp(msg.getJMSTimestamp());
            setOutput(output);

            session.close();
        } catch (JMSException ex) {
            throw new BreakdownException("Failed to execute test case", ex);
        }
    }
}
