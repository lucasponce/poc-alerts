package org.poc.impl;

import org.poc.ServiceFactory;
import org.poc.api.*;
import org.poc.cep.CepEngine;
import org.poc.cep.CepEngineFactory;
import org.poc.model.condition.Alert;
import org.poc.model.condition.ThresholdCondition;
import org.poc.model.data.Metric;
import org.poc.model.data.State;
import org.poc.model.trigger.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A proof of concept of AlertsService to draft some possible designs
 */
public class PocAlertsService implements AlertsService {
    private static final Logger LOG = LoggerFactory.getLogger(PocAlertsService.class);

    private List<Trigger> triggers;
    private List<ThresholdCondition> thresholds;

    private final List<Metric> pending;
    private final List<State> states;
    private final List<Alert> alerts;

    private final List<Metric> processed;

    public static final int DELAY = 1000;
    public static final int PERIOD = 2000;

    private final Timer wakeUpTimer;
    private TimerTask cepTask;

    private final CepEngine cepEngine;

    private final RulesStoreService rulesStoreService;
    private final NotificationsService notificationsService;

    public PocAlertsService() {
        triggers = new CopyOnWriteArrayList<>();
        thresholds = new CopyOnWriteArrayList<>();
        pending = new CopyOnWriteArrayList<>();
        states = new CopyOnWriteArrayList<>();
        alerts = new CopyOnWriteArrayList<>();
        processed = new CopyOnWriteArrayList<>();

        notificationsService = ServiceFactory.getNotificationsService();

        cepEngine = CepEngineFactory.getCepEngine();

        rulesStoreService = ServiceFactory.getRulesStoreService();
        Map<String, String> initRules = rulesStoreService.getAllRules();

        for (String name : initRules.keySet()) {
            cepEngine.addRule(name, initRules.get(name));
        }

        cepEngine.addGlobal("states", states);
        cepEngine.addGlobal("alerts", alerts);
        cepEngine.addGlobal("notificationsService", notificationsService);

        wakeUpTimer = new Timer("PocAlertsService-Timer");

        cepTask = new CepInvoker();
        wakeUpTimer.schedule(cepTask, DELAY, PERIOD);
    }

    @Override
    public void sendMetric(Metric metric) {
        if (metric == null) {
            throw new IllegalArgumentException("Metric must be non null");
        }
        pending.add(metric);
    }

    @Override
    public void sendMetrics(Collection<Metric> metrics) {
        if (metrics == null) {
            throw new IllegalArgumentException("Metric must be non null");
        }
        pending.addAll(metrics);
    }

    @Override
    public void addTrigger(Trigger trigger) {
        if (trigger == null) {
            throw new IllegalArgumentException("Trigger must be non null");
        }
        triggers.add(trigger);
        cepEngine.addFact(trigger);
    }

    @Override
    public void addTriggers(Collection<Trigger> triggers) {
        if (triggers == null) {
            throw new IllegalArgumentException("Trigger must be non null");
        }
        this.triggers.addAll(triggers);
        cepEngine.addFacts(triggers);
    }

    @Override
    public void removeTrigger(String id) {
        int found = -1;
        Trigger t = null;
        for (int i=0; i<triggers.size(); i++) {
            t = triggers.get(i);
            if (t.getId().equals(id)) {
                found = i;
                break;
            }
        }
        if (found != -1) {
            triggers.remove(found);
            cepEngine.removeFact(t);
        }
    }

    @Override
    public Trigger getTrigger(String id) {
        int found = -1;
        Trigger t = null;
        for (int i=0; i<triggers.size(); i++) {
            t = triggers.get(i);
            if (t.getId().equals(id)) {
                found = i;
                break;
            }
        }
        if (found != -1) {
            return t;
        } else {
            return null;
        }
    }

    /*
     * This implementation is very not efficient.
     * We should modify facts using fact handles.
     * In the other side, in this stage of the PoC I wanted to not introduce specific drools.* classes in the CepEngine interface.
     * So, we should be able to map "FactHandle" concept perhaps with id for specific facts as Triggers or others.
     */
    @Override
    public void updateTrigger(String id, Trigger trigger) {
        if (id == null) {
            throw new IllegalArgumentException("Trigger id must be not null");
        }
        if (trigger == null || trigger.getId() == null) {
            throw new IllegalArgumentException("Trigger must be not-null");
        }
        if (!trigger.getId().equals(id)) {
            throw new IllegalArgumentException("Trigger id and updated trigger must be equal");
        }
        removeTrigger(id);
        addTrigger(trigger);
    }

    @Override
    public void addThresholdCondition(ThresholdCondition thresholdCondition) {
        if (thresholdCondition == null) {
            throw new IllegalArgumentException("ThresholdCondition must be non null");
        }
        thresholds.add(thresholdCondition);
        cepEngine.addFact(thresholdCondition);
    }

    @Override
    public void addThresholdConditions(Collection<ThresholdCondition> thresholdConditions) {
        if (thresholdConditions == null) {
            throw new IllegalArgumentException("ThresholdCondition must be non null");
        }
        thresholds.addAll(thresholdConditions);
        cepEngine.addFacts(thresholdConditions);
    }

    @Override
    public void removeThresholdCondition(String triggerId, String metricId) {
        int found = -1;
        ThresholdCondition tc = null;
        for (int i=0; i<thresholds.size(); i++) {
            tc = thresholds.get(i);
            if (tc.getTriggerId().equals(triggerId) && tc.getMetricId().equals(metricId)) {
                found = i;
                break;
            }
        }
        if (found != -1) {
            thresholds.remove(found);
            cepEngine.removeFact(tc);
        }
    }

    @Override
    public Collection<State> checkState() {
        return states;
    }

    @Override
    public Collection<Alert> checkAlert() {
        return alerts;
    }

    @Override
    public void reloadRules() {
        cepEngine.reset();
        cepTask.cancel();

        Map<String, String> initRules = rulesStoreService.getAllRules();

        for (String name : initRules.keySet()) {
            cepEngine.addRule(name, initRules.get(name));
        }

        cepEngine.addGlobal("states", states);
        cepEngine.addGlobal("alerts", alerts);
        cepEngine.addGlobal("notificationsService", notificationsService);

        // Re-inserting triggers and thresholds
        cepEngine.addFacts(triggers);
        cepEngine.addFacts(thresholds);

        cepTask = new CepInvoker();
        wakeUpTimer.schedule(cepTask, DELAY, PERIOD);
    }

    @Override
    public void clear() {
        cepTask.cancel();

        cepEngine.clear();

        pending.clear();
        states.clear();
        alerts.clear();
        processed.clear();

        cepTask = new CepInvoker();
        wakeUpTimer.schedule(cepTask, DELAY, PERIOD);
    }

    /**
     * This task is responsible to "wakeup" and send all pending events to CepEngine and manage results (alerts/status).
     */
    public class CepInvoker extends TimerTask {

        public CepInvoker() {
        }

        @Override
        public void run() {

            if (pending.size() > 0) {
                for (int i=0; i<pending.size(); i++) {
                    Metric m = pending.get(i);
                    LOG.info("Adding to CEP ... " + m);
                    cepEngine.addFact(m);
                    processed.add(m);
                }

                pending.clear();

                try {
                    cepEngine.fire();
                } catch (Exception e) {
                    LOG.error("Error on CEP processing ", e);
                }
            }

        }
    }
}
