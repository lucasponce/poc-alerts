package org.poc.api;

import java.util.Map;

/**
 * Interface that defines functionality of rules storage strategy.
 */
public interface RulesStoreService {

    void addRule(String name, String rule);

    void removeRule(String name);

    String getRule(String name);

    Map<String, String> getAllRules();
}
