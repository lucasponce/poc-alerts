package org.poc.api;

import org.poc.impl.PocNotificationsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A basic factory for NotificationsService implementations
 */
public class NotificationsFactory {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationsFactory.class);

    private static NotificationsService notificationsService;

    public static NotificationsService getNotificationsService() {
        if (notificationsService == null) {
            notificationsService = new PocNotificationsService();
            LOG.info("NotificationsService created");
        }
        return notificationsService;
    }

}
