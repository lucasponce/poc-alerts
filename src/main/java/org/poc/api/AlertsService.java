package org.poc.api;

import java.util.Collection;

/**
 * Interface that defines the functionality of Alerts Service
 */
public interface AlertsService {

    void sendEvent(Event event);

    void sendBatch(Collection<Event> events);

    Collection<State> checkState();

    void finish();
}
