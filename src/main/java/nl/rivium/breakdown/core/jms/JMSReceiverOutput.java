package nl.rivium.breakdown.core.jms;

import nl.rivium.breakdown.core.Output;
import nl.rivium.breakdown.core.PropertyAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

public class JMSReceiverOutput extends Output {
    private String payload;

    @XmlJavaTypeAdapter(PropertyAdapter.class)
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
        return String.format("Properties: %d, Content: '%s'", getProperties().size(), getPayload());
    }
}
