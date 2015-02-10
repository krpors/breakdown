package nl.rivium.breakdown.core.jms;

import nl.rivium.breakdown.core.Output;

import java.util.HashMap;
import java.util.Map;

public class JMSReceiverOutput implements Output {
    private String payload;
    private Map<String, String> properties = new HashMap<>();

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return String.format("%s (Properties: %d, Content: '%s'", JMSReceiverOutput.class.getName(), getProperties().size(), getPayload());
    }
}
