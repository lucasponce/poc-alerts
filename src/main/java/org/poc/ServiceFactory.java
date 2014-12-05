package org.poc;

import org.poc.api.AlertsService;
import org.poc.api.NotificationsService;
import org.poc.api.RulesStoreService;
import org.poc.impl.PocAlertsService;
import org.poc.impl.PocNotificationsService;
import org.poc.impl.PocRulesStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A basic factory for AlertsService, NotificationsService and RulesStoreService implementations.
 * This is main entry point for API users.
 */
public class ServiceFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceFactory.class);

    private static AlertsService alertsService;
    private static NotificationsService notificationsService;
    private static RulesStoreService rulesStoreService;

    public static AlertsService getAlertsService() {
        if (alertsService == null) {
            alertsService = new PocAlertsService();
            LOG.info("AlertsService created.");
        }
        return alertsService;
    }

    public static NotificationsService getNotificationsService() {
        if (notificationsService == null) {
            notificationsService = new PocNotificationsService();
            LOG.info("NotificationsService created");
        }
        return notificationsService;
    }

    public static RulesStoreService getRulesStoreService() {
        if (rulesStoreService == null) {
            rulesStoreService = new PocRulesStoreService();
            LOG.info("RulesStoreService created.");
        }
        return rulesStoreService;
    }
}
