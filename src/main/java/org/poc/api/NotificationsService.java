package org.poc.api;

/**
 * Interface that defines basic functionality of a pluggable notifications system.
 * NotificationsService will be responsible of notifications strategy.
 */
public interface NotificationsService {

    /**
     * Register a new NotificationTask
     */
    void register(NotificationTask task);

    /**
     * Send a notification to the queue
     */
    void notify(String id, String msg);

    /**
     * Remove all NotificationTask previosly registered
     */
    void unregisterAll();

    /**
     * Clear all pending notifications of the queue.
     */
    void clearPending();

    /**
     * Remove both NotificationsTasks and pending notifications.
     */
    void clearAll();
}
