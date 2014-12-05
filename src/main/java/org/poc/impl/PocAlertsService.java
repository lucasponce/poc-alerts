package org.poc.impl;

import org.poc.ServiceFactory;
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

    private final List<Event> processed;

    public static final int DELAY = 1000;
    public static final int PERIOD = 2000;

    private final Timer wakeUpTimer;
    private TimerTask cepTask;

    private final CepEngine cepEngine;

    private final RulesStoreService rulesStoreService;
    private final NotificationsService notificationsService;

    public PocAlertsService() {
        lEvents = new CopyOnWriteArrayList<>();
        lStates = new CopyOnWriteArrayList<>();
        lAlerts = new CopyOnWriteArrayList<>();
        processed = new CopyOnWriteArrayList<>();

        notificationsService = ServiceFactory.getNotificationsService();

        cepEngine = CepEngineFactory.getCepEngine();

        rulesStoreService = ServiceFactory.getRulesStoreService();
        Map<String, String> initRules = rulesStoreService.getAllRules();

        for (String name : initRules.keySet()) {
            cepEngine.addRule(name, initRules.get(name));
        }

        cepEngine.addGlobal("lStates", lStates);
        cepEngine.addGlobal("lAlerts", lAlerts);
        cepEngine.addGlobal("notificationsService", notificationsService);

        wakeUpTimer = new Timer("PocAlertsService-Timer");

        cepTask = new CepInvoker();
        wakeUpTimer.schedule(cepTask, DELAY, PERIOD);
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
    public void reloadRules() {
        cepEngine.reset();
        cepTask.cancel();

        Map<String, String> initRules = rulesStoreService.getAllRules();

        for (String name : initRules.keySet()) {
            cepEngine.addRule(name, initRules.get(name));
        }

        cepEngine.addGlobal("lStates", lStates);
        cepEngine.addGlobal("lAlerts", lAlerts);
        cepEngine.addGlobal("notificationsService", notificationsService);

        cepTask = new CepInvoker();
        wakeUpTimer.schedule(cepTask, DELAY, PERIOD);
    }

    @Override
    public void clear() {
        cepTask.cancel();

        cepEngine.clear();

        lEvents.clear();
        lStates.clear();
        lAlerts.clear();
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

            if (lEvents.size() > 0) {
                for (int i=0; i<lEvents.size(); i++) {
                    Event e = lEvents.get(i);
                    LOG.info("Adding to CEP ... " + e);
                    cepEngine.addFact(e);
                    processed.add(e);
                }

                lEvents.clear();

                try {
                    cepEngine.fire();
                } catch (Exception e) {
                    LOG.error("Error on CEP processing ", e);
                }
            }

        }
    }
}
