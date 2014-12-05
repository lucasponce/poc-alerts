package org.poc.impl;

import org.poc.api.RulesStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A proof of concept of RulesStoreService to draft rules store concept.
 */
public class PocRulesStoreService implements RulesStoreService {
    private static final Logger LOG = LoggerFactory.getLogger(PocRulesStoreService.class);

    private final Map<String, String> rulesMap;

    public PocRulesStoreService() {
        rulesMap = new ConcurrentHashMap<>();

        initDemoRules();
    }

    @Override
    public void addRule(String name, String rule) {
        rulesMap.put(name, rule);
    }

    @Override
    public void removeRule(String name) {
        rulesMap.remove(name);
    }

    @Override
    public String getRule(String name) {
        return rulesMap.get(name);
    }

    @Override
    public Map<String, String> getAllRules() {
        return rulesMap;
    }

    private void initDemoRules() {
        addRule("JVM", DemoRules.RULE_JVM_MEM);
        addRule("CPU", DemoRules.RULE_CPU);
        addRule("CheckState", DemoRules.RULE_COMBINED);
    }

    /**
     * Helper class to define programatically rules.
     * Rules can be defined statically from storages or dynamically.
     * In drools 6.1.0 versions recommended way is through DRL.
     * In drools 6.2.0 there is a plan to use a POJOs approach to build rules.
     *
     * In this example we have approach of 1 virtual DRL file per rule.
     * This is not obviously an optimized approach, just for demo purposes.
     */
    public class DemoRules {
        public static final String NL              = "\n";
        public static final String PKG             = "package org.poc.rules" + NL + NL;
        public static final String IMPORTS         = "import org.poc.api.Alert" + NL +
                                                        "import org.poc.api.Event" + NL +
                                                        "import org.poc.api.State" + NL + NL;
        public static final String GLOBALS         = "global java.util.List lStates" + NL +
                                                        "global java.util.List lAlerts" + NL +
                                                        "global org.poc.api.NotificationsService notificationsService" + NL + NL;

        public static final String RULE_JVM_MEM    = PKG + IMPORTS + GLOBALS +
                                                        "rule \"High JVM\"" + NL +
                                                        "when" + NL +
                                                        "$e : Event( id == \"JVM\", $value : value > 10 )" + NL +
                                                        "then" + NL +
                                                        "Alert alert = new Alert(\"JVM\", \"Mem value = \" + $value);" + NL +
                                                        "lAlerts.add(alert);" + NL +
                                                        "notificationsService.notify(\"SNMP-Trap-JVM\");" + NL +
                                                        "insert(alert);" + NL +
                                                        "end";

        public static final String RULE_CPU        = PKG + IMPORTS + GLOBALS +
                                                        "rule \"High CPU\"" + NL +
                                                        "when" + NL +
                                                        "$e : Event( id == \"CPU\", $value : value > 90 )" + NL +
                                                        "then" + NL +
                                                        "Alert alert = new Alert(\"CPU\", \"CPU value = \" + $value);" + NL +
                                                        "lAlerts.add(alert);" + NL +
                                                        "notificationsService.notify(\"SNMP-Trap-CPU\");" + NL +
                                                        "insert(alert);" + NL +
                                                        "end";

        public static final String RULE_COMBINED   = PKG + IMPORTS + GLOBALS +
                                                        "rule \"Bad State\"" + NL +
                                                        "when" + NL +
                                                        "$a1 : Alert( id == \"JVM\")" + NL +
                                                        "$a2 : Alert( id == \"CPU\")" + NL +
                                                        "then" + NL +
                                                        "State state = new State(\"Bad State\", \"CPU  and JVM metrics under high load\");" + NL +
                                                        "lStates.add(state);" + NL +
                                                        "notificationsService.notify(\"admin@email.com\");" + NL +
                                                        "insert(state);" + NL +
                                                        "end";
    }


}
