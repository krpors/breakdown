package nl.rivium.breakdown.core;

import java.util.HashMap;
import java.util.Map;

public class JMSSenderInput extends Input {
    private Map<String, String> properties = new HashMap<>();
    private String payload;

    public Map<String, String> getProperties() {
        return properties;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return String.format("%s (Content: '%s')", JMSSenderInput.class.getName(), getPayload());
    }
}
