package org.poc.tests;

import org.junit.Assert;
import org.junit.Test;
import org.poc.ServiceFactory;
import org.poc.api.*;
import org.poc.model.condition.ThresholdCondition;
import org.poc.model.condition.ThresholdCondition.Operator;
import org.poc.model.data.Metric;
import org.poc.model.trigger.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Basic tests for AlertsService
 */
public class AlertsServiceTest {
    private static final Logger LOG = LoggerFactory.getLogger(AlertsServiceTest.class);

    @Test
    public void twoAlertTest() throws Exception {

        /*
         * Create notifications to register into NotificationsService.
         */
        SnmpNotification snmpTrap1 = new SnmpNotification("SNMP-Trap-1");
        SnmpNotification snmpTrap2 = new SnmpNotification("SNMP-Trap-2");
        EmailNotification emailAdmin = new EmailNotification("admin@email.com");

        NotificationsService notificationService = ServiceFactory.getNotificationsService();

        notificationService.register(snmpTrap1);
        notificationService.register(snmpTrap2);
        notificationService.register(emailAdmin);

        AlertsService alertsService = ServiceFactory.getAlertsService();

        /*
         * Create alert definitions in the form of Trigger and ThresholdCondition
         */
        Trigger t1 = new Trigger("trigger-1", "metric-01-low");
        t1.addNotifier("SNMP-Trap-1");
        ThresholdCondition cs1c1 = new ThresholdCondition("trigger-1", "Metric-01", 1, 1, Operator.LT, 5.0);

        alertsService.addTrigger(t1);
        alertsService.addThresholdCondition(cs1c1);

        Trigger t2 = new Trigger("trigger-2", "metric-01-02-high");
        t2.addNotifier("SNMP-Trap-1");
        t2.addNotifier("SNMP-Trap-2");
        t2.addNotifier("admin@email.com");
        ThresholdCondition cs2c1 = new ThresholdCondition("trigger-2", "Metric-01", 2, 1, Operator.GTE, 15.0);
        ThresholdCondition cs2c2 = new ThresholdCondition("trigger-2", "Metric-02", 2, 2, Operator.GTE, 15.0);

        alertsService.addTrigger(t2);
        alertsService.addThresholdCondition(cs2c1);
        alertsService.addThresholdCondition(cs2c2);

        /*
         * Dummy Events creations.
         * These one will come from the messaging system / or API defined to receive events.
         */
        List<Metric> batch = new ArrayList<>();

        Metric normalMetric01 = new Metric("Metric-01", System.currentTimeMillis(), 10d);
        Metric lowMetric01 = new Metric("Metric-01", System.currentTimeMillis(), 4.3d);
        Metric highMetric01 = new Metric("Metric-01", System.currentTimeMillis(), 24.3d);

        batch.add(normalMetric01);
        batch.add(lowMetric01);
        batch.add(highMetric01);

        Metric normalMetric02 = new Metric("Metric-02", System.currentTimeMillis(), 10d);
        Metric lowMetric02 = new Metric("Metric-02", System.currentTimeMillis(), 4.3d);
        Metric highMetric02 = new Metric("Metric-02", System.currentTimeMillis(), 24.3d);

        batch.add(normalMetric02);
        batch.add(lowMetric02);
        batch.add(highMetric02);

        alertsService.sendMetrics(batch);

        Thread.sleep(4000);

        Assert.assertEquals(2, alertsService.checkAlert().size());

        Assert.assertTrue(snmpTrap1.isNotified());
        Assert.assertTrue(snmpTrap2.isNotified());
        Assert.assertTrue(emailAdmin.isNotified());

        notificationService.clearAll();
        alertsService.clear();
    }

    @Test
    public void longTest() throws Exception {

        /*
         * Create notifications to register into NotificationsService.
         */
        SnmpNotification snmpTrap1 = new SnmpNotification("SNMP-Trap-1");
        SnmpNotification snmpTrap2 = new SnmpNotification("SNMP-Trap-2");
        EmailNotification emailAdmin = new EmailNotification("admin@email.com");

        NotificationsService notificationService = ServiceFactory.getNotificationsService();

        notificationService.register(snmpTrap1);
        notificationService.register(snmpTrap2);
        notificationService.register(emailAdmin);

        AlertsService alertsService = ServiceFactory.getAlertsService();

        /*
         * Create alert definitions in the form of Trigger and ThresholdCondition
         */
        Trigger t1 = new Trigger("trigger-1", "metric-01-low");
        t1.addNotifier("SNMP-Trap-1");
        ThresholdCondition cs1c1 = new ThresholdCondition("trigger-1", "Metric-01", 1, 1, Operator.LT, 5.0);

        alertsService.addTrigger(t1);
        alertsService.addThresholdCondition(cs1c1);

        Trigger t2 = new Trigger("trigger-2", "metric-01-02-high");
        t2.addNotifier("SNMP-Trap-1");
        t2.addNotifier("SNMP-Trap-2");
        t2.addNotifier("admin@email.com");
        ThresholdCondition cs2c1 = new ThresholdCondition("trigger-2", "Metric-01", 2, 1, Operator.GTE, 15.0);
        ThresholdCondition cs2c2 = new ThresholdCondition("trigger-2", "Metric-02", 2, 2, Operator.GTE, 15.0);

        alertsService.addTrigger(t2);
        alertsService.addThresholdCondition(cs2c1);
        alertsService.addThresholdCondition(cs2c2);

        /*
         * We are going to send metrics for 20 seconds
         */
        long startTime = System.currentTimeMillis();
        long totalTime = 20 * 1000; // 20 Seconds

        while (( startTime + totalTime ) > System.currentTimeMillis()) {
            /*
             * Generate metrics
             */
            Metric ramdomMetric01 = new Metric("Metric-01", System.currentTimeMillis(), 20d * Math.random());
            Metric randomMetric02 = new Metric("Metric-02", System.currentTimeMillis(), 20d * Math.random());

            alertsService.sendMetric(ramdomMetric01);
            alertsService.sendMetric(randomMetric02);

            /*
             * Check notifications and re-activate trigger
             */
            if (snmpTrap1.isNotified() && snmpTrap2.isNotified() && emailAdmin.isNotified()) {
                if (!t1.isActive() && !t2.isActive()) {
                    t1.setActive(true);
                    t2.setActive(true);
                    alertsService.updateTrigger(t1.getId(), t1);
                    alertsService.updateTrigger(t2.getId(), t2);
                }
            }

            Thread.sleep(50);
        }

        notificationService.clearAll();
        alertsService.clear();
    }



    /*
        Samples of pluggable notification implementations
     */

    public class SnmpNotification implements NotificationTask {

        private String id;
        private boolean notified;

        public SnmpNotification(String id) {
            this.id = id;
            this.notified = false;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public void run() {
            LOG.info("=== [ " + id + " ] sent ===");
            notified = true;
        }

        public boolean isNotified() {
            return notified;
        }
    }

    public class EmailNotification implements NotificationTask {

        private String email;
        private boolean notified;

        public EmailNotification(String email) {
            this.email = email;
            this.notified = false;
        }

        @Override
        public String getId() {
            return email;
        }

        @Override
        public void run() {
            LOG.info("=== [ " + email + " ] sent ===");
            notified = true;
        }

        public boolean isNotified() {
            return notified;
        }
    }

}
