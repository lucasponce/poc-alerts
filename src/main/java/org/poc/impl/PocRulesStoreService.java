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
        addRule("Threshold", DemoRules.THRESHOLD);
        addRule("AlertOneCondition", DemoRules.ALERT_ONE_CONDITION);
        addRule("AlertTwoCondition", DemoRules.ALERT_TWO_CONDITION);
    }

    @Override
    public void clear() {
        rulesMap.clear();
    }

    @Override
    public void reset() {
        clear();
        initDemoRules();
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
        public static final String IMPORTS         = "import org.poc.model.condition.Alert" + NL +
                                                        "import org.poc.model.condition.ConditionMatch" + NL +
                                                        "import org.poc.model.condition.ThresholdCondition" + NL +
                                                        "import org.poc.model.data.Metric" + NL +
                                                        "import org.poc.model.trigger.Trigger" + NL +
                                                        "import org.poc.model.data.State" + NL + NL;

        public static final String GLOBALS         = "global java.util.List states" + NL +
                                                        "global java.util.List alerts" + NL +
                                                        "global org.poc.api.NotificationsService notificationsService" + NL + NL;


        public static final String FUNCTIONS       = "import function org.poc.model.condition.ThresholdCondition.match" + NL + NL;


        public static final String THRESHOLD       = PKG + IMPORTS + GLOBALS + FUNCTIONS +
                                                        "rule \"Threshold\"" + NL +
                                                        "when" + NL +
                                                        "$t  : Trigger( active == true, $tid : id )" + NL +
                                                        "$tc : ThresholdCondition( triggerId == $tid, $mid : metricId, $th : threshold, $op : operator )" + NL +
                                                        "$m  : Metric( $mid == id, $value : value )" + NL +
                                                        "eval( match( $op, $th, $value ) )" + NL +
                                                        "then" + NL +
                                                        "String log = $tc.getLog( $value );" + NL +
                                                        "ConditionMatch cm = new ConditionMatch( $tc, log );" + NL +
                                                        "insert(cm);" + NL +
                                                        "end";

        public static final String ALERT_ONE_CONDITION  = PKG + IMPORTS + GLOBALS + FUNCTIONS +
                                                        "rule \"AlertOneCondition\"" + NL +
                                                        "when" + NL +
                                                        "$t  : Trigger( active == true, $tid : id )" + NL +
                                                        "$cm : ConditionMatch( triggerId == $tid, conditionSetSize == 1, conditionSetIndex == 1)" + NL +
                                                        "then" + NL +
                                                        "$t.setActive( false );" + NL +
                                                        "Alert alert = new Alert( $tid );" + NL +
                                                        "alert.addConditionMatch( $cm );" + NL +
                                                        "for (String notifierId : $t.getNotifiers()) {" + NL +
                                                        "notificationsService.notify( notifierId );" + NL +
                                                        "}" + NL +
                                                        "alerts.add( alert );" + NL +
                                                        "insert( alert );" + NL +
                                                        "update( $t );" + NL +
                                                        "end";

        public static final String ALERT_TWO_CONDITION  = PKG + IMPORTS + GLOBALS + FUNCTIONS +
                                                        "rule \"AlertTwoCondition\"" + NL +
                                                        "when" + NL +
                                                        "$t  : Trigger( active == true, $tid : id )" + NL +
                                                        "$cm1 : ConditionMatch( triggerId == $tid, conditionSetSize == 2, conditionSetIndex == 1)" + NL +
                                                        "$cm2 : ConditionMatch( triggerId == $tid, conditionSetSize == 2, conditionSetIndex == 2)" + NL +
                                                        "then" + NL +
                                                        "$t.setActive( false );" + NL +
                                                        "Alert alert = new Alert( $tid );" + NL +
                                                        "alert.addConditionMatch( $cm1 );" + NL +
                                                        "alert.addConditionMatch( $cm2 );" + NL +
                                                        "for (String notifierId : $t.getNotifiers()) {" + NL +
                                                        "notificationsService.notify( notifierId );" + NL +
                                                        "}" + NL +
                                                        "alerts.add( alert );" + NL +
                                                        "insert( alert );" + NL +
                                                        "update( $t );" + NL +
                                                        "end";

    }


}
