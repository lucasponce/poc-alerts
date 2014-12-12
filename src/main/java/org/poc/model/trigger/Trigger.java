package org.poc.model.trigger;

/**
 * It defines conditions and notifiers attached that will create an alert.
 */
public class Trigger extends TriggerTemplate {

    /**
     * Id must be unique
     */
    private String id;
    private boolean active;

    public Trigger(String id, String name) {
        super(name);

        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Trigger id must be non-empty.");
        }
        this.id = id;
        this.active = true;
    }

    public Trigger(String id, String name, boolean active) {
        super(name);

        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Trigger id must be non-empty.");
        }
        this.id = id;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
