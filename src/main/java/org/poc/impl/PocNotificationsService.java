package org.poc.impl;

import org.poc.api.NotificationTask;
import org.poc.api.NotificationsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A proof of concept of a pluggable notifications service.
 */
public class PocNotificationsService implements NotificationsService {
    private static final Logger LOG = LoggerFactory.getLogger(PocNotificationsService.class);

    private final Map<String, NotificationTask> notifications;
    private final Queue<String> pending;

    public static final int DELAY = 1000;
    public static final int PERIOD = 2000;

    private final Timer wakeUpTimer;

    public PocNotificationsService() {
        notifications = new ConcurrentHashMap<String, NotificationTask>();
        pending = new ConcurrentLinkedQueue<>();

        wakeUpTimer = new Timer("PocNotificationsService-Timer");
        wakeUpTimer.schedule(new NotificationsInvoker(), DELAY, PERIOD);
    }

    @Override
    public void register(NotificationTask notification) {
        LOG.info("Registering ... " + notification);

        if (notification == null) {
            throw new IllegalArgumentException("Notification must not be null");
        }

        String id = notification.getId();
        if (notifications.containsKey(id)) {
            throw new IllegalArgumentException("Notification " + id + " previously registered");
        }
        notifications.put(id, notification);
    }

    @Override
    public void notify(String id) {
        if (!notifications.containsKey(id)) {
            LOG.warn("Notification [ " + id + " ] is not registered ! ...");
            return;
        }
        LOG.info("Notification [ " + id + " ] sent to the queue");
        pending.add(id);
    }

    @Override
    public void finish() {
    }

    /**
     * This task is reponsible to check pending events in queue and invoke notifications
     */
    public class NotificationsInvoker extends TimerTask {

        @Override
        public void run() {

            while (pending.size() > 0) {
                String id = pending.poll();
                try {
                    notifications.get(id).run();
                } catch (Exception e) {
                    LOG.error("Error invoking notification " + id, e);
                }
            }

        }
    }
}
