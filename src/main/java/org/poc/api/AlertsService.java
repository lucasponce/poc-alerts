package org.poc.api;

import org.poc.model.condition.Alert;
import org.poc.model.condition.ThresholdCondition;
import org.poc.model.data.Metric;
import org.poc.model.data.State;
import org.poc.model.trigger.Trigger;

import java.util.Collection;

/**
 * Interface that defines the functionality of Alerts Service
 */
public interface AlertsService {

    void sendMetric(Metric metric);
    void sendMetrics(Collection<Metric> metrics);

    void addTrigger(Trigger trigger);
    void addTriggers(Collection<Trigger> triggers);
    void removeTrigger(String id);

    void addThresholdCondition(ThresholdCondition thresholdCondition);
    void addThresholdConditions(Collection<ThresholdCondition> thresholdConditions);
    void removeThresholdCondition(String triggerId, String metricId);

    Collection<State> checkState();
    Collection<Alert> checkAlert();

    /**
     * Reloads all rules available through RulesStoreService
     */
    void reloadRules();

    /**
     * Clear session state
     */
    void clear();
}
