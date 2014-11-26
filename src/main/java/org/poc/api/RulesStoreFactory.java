package org.poc.api;

import org.poc.impl.PocRulesStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A basic factory for RulesStoreService implementations
 */
public class RulesStoreFactory {
    private static final Logger LOG = LoggerFactory.getLogger(RulesStoreFactory.class);

    private static RulesStoreService rulesStoreService;

    public static RulesStoreService getRulesStoreService() {
        if (rulesStoreService == null) {
            rulesStoreService = new PocRulesStoreService();
            LOG.info("RulesStoreService created.");
        }
        return rulesStoreService;
    }
}
