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

    private final Map<String, NotificationTask> register;
    private final Queue<NotificationMessage> pending;

    public static final int DELAY = 1000;
    public static final int PERIOD = 2000;

    private final Timer wakeUpTimer;
    private TimerTask notificationsTask;

    public PocNotificationsService() {
        register = new ConcurrentHashMap<String, NotificationTask>();
        pending = new ConcurrentLinkedQueue<>();

        wakeUpTimer = new Timer("PocNotificationsService-Timer");
        notificationsTask = new NotificationsInvoker();
        wakeUpTimer.schedule(notificationsTask, DELAY, PERIOD);
    }

    @Override
    public void register(NotificationTask task) {
        LOG.info("Registering ... " + task);

        if (task == null) {
            throw new IllegalArgumentException("NotificationTask must not be null");
        }

        String id = task.getId();
        if (register.containsKey(id)) {
            throw new IllegalArgumentException("NotificationTask " + id + " previously registered");
        }
        register.put(id, task);
    }

    @Override
    public void notify(String id, String msg) {
        if (!register.containsKey(id)) {
            LOG.warn("Notification [ " + id + " ] is not registered ! ...");
            return;
        }
        LOG.info("Notification [ " + id + " ] sent to the queue");
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.id = id;
        notificationMessage.msg = msg;
        pending.add(notificationMessage);
    }

    @Override
    public void unregisterAll() {
        register.clear();
    }

    @Override
    public void clearPending() {
        pending.clear();
    }

    @Override
    public void clearAll() {
        clearPending();
        unregisterAll();
    }

    /**
     * This task is reponsible to check pending events in queue and invoke notifications
     */
    public class NotificationsInvoker extends TimerTask {

        @Override
        public void run() {

            while (pending.size() > 0) {
                NotificationMessage notificationMessage = pending.poll();
                String id = notificationMessage.id;
                String msg = notificationMessage.msg;
                try {
                    NotificationTask notificationTask = register.get(id);
                    notificationTask.setMessage(msg);
                    notificationTask.run();
                } catch (Exception e) {
                    LOG.error("Error invoking notification " + id, e);
                }
            }

        }
    }

    /**
     * A helper class to enqueue notifications messages
     */
    public class NotificationMessage {
        public String id;
        public String msg;
    }
}
