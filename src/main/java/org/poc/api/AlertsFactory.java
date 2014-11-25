package org.poc.api;

import org.poc.impl.PocAlertsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A basic factory for AlertService implementations
 */
public class AlertsFactory {
    private static final Logger LOG = LoggerFactory.getLogger(AlertsFactory.class);

    private static AlertsService alertsService;

    public static AlertsService getAlertsService() {
        if (alertsService == null) {
            LOG.info("AlertsService created.");
            alertsService = new PocAlertsService();
        }
        return alertsService;
    }

}
