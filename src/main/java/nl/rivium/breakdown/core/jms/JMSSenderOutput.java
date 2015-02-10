package nl.rivium.breakdown.core.jms;

import nl.rivium.breakdown.core.Output;

public class JMSSenderOutput implements Output {
    private String jmsMessageId;
    private long jmsTimestamp;

    public String getJmsMessageId() {
        return jmsMessageId;
    }

    public void setJmsMessageId(String jmsMessageId) {
        this.jmsMessageId = jmsMessageId;
    }

    public long getJmsTimestamp() {
        return jmsTimestamp;
    }

    public void setJmsTimestamp(long jmsTimestamp) {
        this.jmsTimestamp = jmsTimestamp;
    }

    @Override
    public String toString() {
        return String.format("%s (Message ID: %s, Timestamp: %d", JMSSenderOutput.class.getName(), getJmsMessageId(), getJmsTimestamp());
    }
}
