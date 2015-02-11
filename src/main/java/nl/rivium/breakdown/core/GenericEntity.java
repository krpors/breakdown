package nl.rivium.breakdown.core;

/**
 * A generic entity. Most of the things in the core have basic properties. These properties are contained within this
 * abstract class.
 */
public abstract class GenericEntity {
    private String name;
    private String description;

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
}
