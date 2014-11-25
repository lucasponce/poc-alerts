package org.poc.impl;

import org.poc.api.AlertsService;
import org.poc.api.Event;
import org.poc.api.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * A proof of concept of AlertsService to draft some possible designs
 */
public class PocAlertsService implements AlertsService {
    private static final Logger LOG = LoggerFactory.getLogger(PocAlertsService.class);

    @Override
    public void sendEvent(Event e) {

    }

    @Override
    public void sendBatch(Collection<Event> events) {

    }

    @Override
    public Collection<State> checkState() {
        return null;
    }
}
