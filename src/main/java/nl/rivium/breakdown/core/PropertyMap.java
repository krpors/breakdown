package nl.rivium.breakdown.core;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Custom Property'Map' class to allow JAXB to marshall properties using attributes.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyMap {
    @XmlElement(name = "property")
    private List<PropertyEntry> properties;

    public List<PropertyEntry> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyEntry> properties) {
        this.properties = properties;
    }
}
