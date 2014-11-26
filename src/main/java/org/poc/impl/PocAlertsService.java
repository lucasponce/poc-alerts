package org.poc.impl;

import org.poc.api.*;
import org.poc.cep.CepEngine;
import org.poc.cep.CepEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A proof of concept of AlertsService to draft some possible designs
 */
public class PocAlertsService implements AlertsService {
    private static final Logger LOG = LoggerFactory.getLogger(PocAlertsService.class);

    private final List<Event> lEvents;
    private final List<State> lStates;
    private final List<Alert> lAlerts;

    public static final int DELAY = 1000;
    public static final int PERIOD = 2000;

    private final Timer wakeUpTimer;

    private final CepEngine cepEngine;

    private final RulesStoreService rulesStoreService;

    public PocAlertsService() {
        lEvents = new CopyOnWriteArrayList<>();
        lStates = new CopyOnWriteArrayList<>();
        lAlerts = new CopyOnWriteArrayList<>();

        cepEngine = CepEngineFactory.getCepEngine();

        rulesStoreService = RulesStoreFactory.getRulesStoreService();
        Map<String, String> initRules = rulesStoreService.getAllRules();

        for (String name : initRules.keySet()) {
            cepEngine.addRule(name, initRules.get(name));
        }

        cepEngine.addGlobal("lStates", lStates);
        cepEngine.addGlobal("lAlerts", lAlerts);

        wakeUpTimer = new Timer("PocAlertsService-Timer");
        wakeUpTimer.schedule(new CepInvoker(), DELAY, PERIOD);
    }

    @Override
    public void sendEvent(Event e) {
        lEvents.add(e);
    }

    @Override
    public void sendBatch(Collection<Event> events) {
        lEvents.addAll(events);
    }

    @Override
    public Collection<State> checkState() {
        return lStates;
    }

    @Override
    public Collection<Alert> checkAlert() {
        return lAlerts;
    }

    @Override
    public void finish() {
        wakeUpTimer.cancel();
    }

    /**
     * This task is responsible to "wakeup" and send all pending events to CepEngine and manage results (alerts/status).
     */
    public class CepInvoker extends TimerTask {

        int lastIndex;

        public CepInvoker() {
            lastIndex = 0;
        }

        @Override
        public void run() {
            int newElements = lEvents.size() - lastIndex;

            LOG.info("New events: " + newElements);

            for ( int i = lastIndex; i < lEvents.size(); i++ ) {
                Event e = lEvents.get(i);
                LOG.info("Adding to CEP ... " + e);
                cepEngine.addFact(e);
            }

            try {
                cepEngine.fire();
            } catch (Exception e) {
                LOG.error("Error on CEP processing ", e);
            }

            lastIndex = lEvents.size();
        }

    }
}
