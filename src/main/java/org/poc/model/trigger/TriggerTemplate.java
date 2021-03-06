package org.poc.model.trigger;

import org.poc.model.condition.Condition;

import java.util.HashSet;
import java.util.Set;

/**
 * A base template for Triggers.
 */
public class TriggerTemplate {

    public enum Match {
        ALL, ANY
    };

    private String name;
    private String description;
    private Match match;
    private Set<Condition> conditions;

    /**
     * It stores NotificationTask id, as notifiers can be complex and they will be registered through NotificationsService.
     */
    private Set<String> notifiers;

    public TriggerTemplate(String name) {
        this.name = name;
        this.match = Match.ALL;
        this.conditions = new HashSet<>();
        this.notifiers = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Trigger name must be non-empty.");
        }
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Set<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(Set<Condition> conditions) {
        this.conditions = conditions;
    }

    public void addCondition(Condition condition) {
        if (condition == null) {
            return;
        }
        conditions.add(condition);
    }

    public void addConditions(Set<Condition> conditions) {
        if (conditions == null) {
            return;
        }
        this.conditions.addAll(conditions);
    }

    public void removeCondition(Condition condition) {
        if (condition == null) {
            return;
        }
        conditions.remove(condition);
    }

    public Set<String> getNotifiers() {
        return notifiers;
    }

    public void setNotifiers(Set<String> notifiers) {
        this.notifiers = notifiers;
    }

    public void addNotifier(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Notifier id must be non-empty.");
        }
        notifiers.add(id);
    }

    public void addNotifiers(Set<String> ids) {
        if (ids == null) {
            return;
        }
        notifiers.addAll(ids);
    }

    public void removeNotifier(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Notifier id must be non-empty.");
        }
        notifiers.remove(id);
    }
}
