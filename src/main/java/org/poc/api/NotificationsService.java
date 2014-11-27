package org.poc.api;

/**
 * Interface that defines basic functionality of a pluggable notifications system.
 * NotificationsService will be responsible of notifications strategy.
 */
public interface NotificationsService {

    void register(NotificationTask notification);

    void notify(String id);

    void finish();
}
