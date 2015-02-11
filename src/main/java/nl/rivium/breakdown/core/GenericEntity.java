package nl.rivium.breakdown.core;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A generic entity. Most of the things in the core have basic properties. These properties are contained within this
 * abstract class.
 */
public abstract class GenericEntity {
    private String name;
    private String description;

    /**
     * This is a parent 'node' of an existing entity. Can be null if there is no parent (for instance, a Project).
     */
    @XmlTransient
    private GenericEntity parent;

    public GenericEntity() {

    }

    public GenericEntity(String name, String description) {
        setName(name);
        setDescription(description);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GenericEntity getParent() {
        return parent;
    }

    public void setParent(GenericEntity parent) {
        this.parent = parent;
    }

    /**
     * This is automatically called by JAXB as soon as the marshaller is finished. This way we can get the parent node
     * and assign it.
     *
     * @param um     The Unmarshaller object.
     * @param parent The parent.
     */
    private void afterUnmarshal(Unmarshaller um, Object parent) {
        if (parent instanceof GenericEntity) {
            GenericEntity entity = (GenericEntity) parent;
            System.out.println("Setting parent entity of " + this + " to " + entity);
            setParent(entity);
        }
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", getClass().getName(), getName());
    }
}
