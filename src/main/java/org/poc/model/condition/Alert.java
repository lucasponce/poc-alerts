package org.poc.model.condition;

import java.util.HashSet;
import java.util.Set;

/**
 * An alert representation thrown from AlertsService
 */
public class Alert {

    private String triggerId;
    private Set<ConditionMatch> matches;
    private long time;

    public Alert(String triggerId) {
        this.triggerId = triggerId;
        this.matches = new HashSet<>();
        this.time = System.currentTimeMillis();
    }

    public String getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(String triggerId) {
        this.triggerId = triggerId;
    }

    public Set<ConditionMatch> getMatches() {
        return matches;
    }

    public void setMatches(Set<ConditionMatch> matches) {
        this.matches = matches;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void addConditionMatch(ConditionMatch conditionMatch) {
        if (conditionMatch == null) {
            throw new IllegalArgumentException("ConditionMatch must be non-empty.");
        }
        matches.add(conditionMatch);
    }

    public void addConditionMatches(Set<ConditionMatch> conditionMatches) {
        if (conditionMatches == null) {
            throw new IllegalArgumentException("ConditionMatch must be non-empty.");
        }
        matches.addAll(conditionMatches);
    }
}
