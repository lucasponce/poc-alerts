package org.poc.model.condition;

/**
 * It represents a matching state of several conditions
 */
public class ConditionMatch extends Condition {

    private String log;
    private Long time;

    public ConditionMatch(Condition condition, String log) {
        this(condition.getTriggerId(), condition.getConditionSetSize(), condition.getConditionSetIndex(), log);
    }

    public ConditionMatch(String triggerId, int conditionSetSize, int conditionSetIndex, String log) {
        super(triggerId, conditionSetSize, conditionSetIndex);
        this.log = log;
        this.time = System.currentTimeMillis();
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
