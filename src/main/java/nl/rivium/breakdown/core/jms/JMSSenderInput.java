package nl.rivium.breakdown.core.jms;

import nl.rivium.breakdown.core.Input;
import nl.rivium.breakdown.core.PropertyAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
public class JMSSenderInput extends Input {

    @XmlJavaTypeAdapter(PropertyAdapter.class)
    @XmlElement(name = "properties")
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
        return String.format("Properties: %d, Content: '%s'", getProperties().size(), getPayload());
    }
}
