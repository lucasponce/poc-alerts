package org.poc.impl;

import org.poc.api.AlertsService;
import org.poc.api.Event;
import org.poc.api.State;
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

    public static final int DELAY = 10;
    public static final int PERIOD = 2000;

    private final Timer wakeUpTimer;

    public PocAlertsService() {
        lEvents = new CopyOnWriteArrayList<>();
        lStates = new CopyOnWriteArrayList<>();

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
                State newState = new State(e.getId(), "State of " + e.getId() + " with " + e.getTimeStamp());
                lStates.add(newState);
            }

            lastIndex = lEvents.size();
        }

    }
}
