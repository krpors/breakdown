package nl.rivium.breakdown.core;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * A generic entity. Most of the things in the core have basic properties. These properties are contained within this
 * abstract class. It also behaves like a Node: it has a parent GenericEntity, but also children GenericEntities. Two
 * generic parameters are defined: P for the Parent type, and C for the Children types. This prevents casting the parent
 * and children types back and forth.
 * <p/>
 * A generic entity is an Observable class. Whenever a class's properties change, it may decide to notify the observers
 * of this class.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class GenericEntity<P extends GenericEntity, C extends GenericEntity> extends Observable {

    private static final long serialVersionUID = -3412144850376073380L;
    /**
     * The entity's name. Default value is empty to avoid the likely-hood of null pointers.
     */
    @XmlElement(defaultValue = "")
    private String name;

    /**
     * Description
     */
    private String description;

    /**
     * This is a parent 'node' of an existing entity. Can be null if there is no parent (for instance, a Project).
     */
    @XmlTransient
    private P parent;

    /**
     * Execution listeners for a project.
     */
    @XmlTransient
    private List<ExecutionListener> executionListeners = new ArrayList<>();

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

    public P getParent() {
        return parent;
    }

    public void setParent(P parent) {
        this.parent = parent;
    }

    public abstract C[] getChildren();

    /**
     * Removes this entity from a parent entity. This may be a no-op. Subclasses may decide to override this to
     * provide custom functionality.
     */
    public void removeFromParent() {
    }

    /**
     * Returns the execution listeners for this Project object.
     *
     * @return The execution listeners. Will never be null, but may be empty.
     */
    public List<ExecutionListener> getExecutionListeners() {
        return executionListeners;
    }

    /**
     * Adds an execution listener to the list.
     *
     * @param e The listener.
     */
    public void addExecutionListener(ExecutionListener e) {
        executionListeners.add(e);
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
            setParent((P) entity);
        }
    }

    /**
     * Method to set the observable to changed, then notify observers immediately.
     */
    public void forceNotifyObservers() {
        setChanged();
        super.notifyObservers();
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", getClass().getName(), getName());
    }
}
