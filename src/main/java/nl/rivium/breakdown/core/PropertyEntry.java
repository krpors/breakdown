package nl.rivium.breakdown.core;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * PropertyEntry defines key/value combination for custom JAXB serialization using attributes.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyEntry {
    @XmlAttribute
    private String key;
    @XmlAttribute
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
