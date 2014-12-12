package org.poc.model.condition;

/**
 * Base class for conditions.
 */
public abstract class Condition {

    /**
     * Owning trigger
     */
    protected String triggerId;

    /**
     * e.g. 2 [conditions]
     */
    protected int conditionSetSize;

    /**
     * e.g. 1 [of 2 conditions]
     */
    protected int conditionSetIndex;

    public Condition(String triggerId, int conditionSetSize, int conditionSetIndex) {
        this.triggerId = triggerId;
        this.conditionSetSize = conditionSetSize;
        this.conditionSetIndex = conditionSetIndex;
    }

    public String getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(String triggerId) {
        this.triggerId = triggerId;
    }

    public int getConditionSetSize() {
        return conditionSetSize;
    }

    public void setConditionSetSize(int conditionSetSize) {
        this.conditionSetSize = conditionSetSize;
    }

    public int getConditionSetIndex() {
        return conditionSetIndex;
    }

    public void setConditionSetIndex(int conditionSetIndex) {
        this.conditionSetIndex = conditionSetIndex;
    }
}
