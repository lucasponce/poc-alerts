package org.poc.tests;

import org.junit.Assert;
import org.junit.Test;
import org.poc.ServiceFactory;
import org.poc.api.*;
import org.poc.impl.PocRulesStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

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
        SnmpNotification snmpTrapJvm = new SnmpNotification("SNMP-Trap-JVM");
        SnmpNotification snmpTrapCpu = new SnmpNotification("SNMP-Trap-CPU");
        EmailNotification emailAdmin = new EmailNotification("admin@email.com");

        NotificationsService notificationService = ServiceFactory.getNotificationsService();

        notificationService.register(snmpTrapJvm);
        notificationService.register(snmpTrapCpu);
        notificationService.register(emailAdmin);

        /**
         * Dummy Events creations.
         * These one will come from the messaging system / or API defined to receive events.
         */
        Event normalJvm = new Event("JVM", 5d, System.currentTimeMillis());
        Event highJvm = new Event("JVM", 12d, System.currentTimeMillis());

        Event normalCpu = new Event("CPU", 50d, System.currentTimeMillis());
        Event highCpu = new Event("CPU", 95d, System.currentTimeMillis());

        /**
         * Events can be received in batch, so a Collection is used here to simulate that scenario.
         */
        Collection<Event> cpu = new ArrayList<>();
        cpu.add(normalCpu);
        cpu.add(highCpu);

        AlertsService alertsService = ServiceFactory.getAlertsService();

        alertsService.sendEvent(normalJvm);
        alertsService.sendEvent(highJvm);
        alertsService.sendBatch(cpu);

        Thread.sleep(4000);

        Assert.assertEquals(2, alertsService.checkAlert().size());
        Assert.assertEquals(1, alertsService.checkState().size());

        Assert.assertTrue(snmpTrapJvm.isNotified());
        Assert.assertTrue(snmpTrapCpu.isNotified());
        Assert.assertTrue(emailAdmin.isNotified());

        notificationService.clearAll();
        alertsService.clear();
    }

    @Test
    public void cloneTest() throws Exception {
        SnmpNotification snmpTrapJvm = new SnmpNotification("SNMP-Trap-JVM");
        SnmpNotification snmpTrapCpu = new SnmpNotification("SNMP-Trap-CPU");
        EmailNotification emailAdmin = new EmailNotification("admin@email.com");

        NotificationsService notificationService = ServiceFactory.getNotificationsService();

        notificationService.register(snmpTrapJvm);
        notificationService.register(snmpTrapCpu);
        notificationService.register(emailAdmin);

        Event normalJvm = new Event("JVM", 5d, System.currentTimeMillis());
        Event highJvm = new Event("JVM", 12d, System.currentTimeMillis());

        Event normalCpu = new Event("CPU", 50d, System.currentTimeMillis());
        Event highCpu = new Event("CPU", 95d, System.currentTimeMillis());

        Collection<Event> cpu = new ArrayList<>();
        cpu.add(normalCpu);
        cpu.add(highCpu);

        AlertsService alertsService = ServiceFactory.getAlertsService();

        alertsService.sendEvent(normalJvm);
        alertsService.sendEvent(highJvm);
        alertsService.sendBatch(cpu);

        Thread.sleep(4000);

        Assert.assertEquals(2, alertsService.checkAlert().size());
        Assert.assertEquals(1, alertsService.checkState().size());

        Assert.assertTrue(snmpTrapJvm.isNotified());
        Assert.assertTrue(snmpTrapCpu.isNotified());
        Assert.assertTrue(emailAdmin.isNotified());

        notificationService.clearAll();
        alertsService.clear();
    }

    @Test
    public void addingRulesInDifferentTimeFrame() throws Exception {
        SnmpNotification snmpTrapJvm = new SnmpNotification("SNMP-Trap-JVM");
        SnmpNotification snmpTrapCpu = new SnmpNotification("SNMP-Trap-CPU");
        EmailNotification emailAdmin = new EmailNotification("admin@email.com");

        NotificationsService notificationService = ServiceFactory.getNotificationsService();

        notificationService.register(snmpTrapJvm);
        notificationService.register(snmpTrapCpu);
        notificationService.register(emailAdmin);

        Event normalJvm = new Event("JVM", 5d, System.currentTimeMillis());
        Event highJvm = new Event("JVM", 12d, System.currentTimeMillis());

        Event normalCpu = new Event("CPU", 50d, System.currentTimeMillis());
        Event highCpu = new Event("CPU", 95d, System.currentTimeMillis());

        Collection<Event> cpu = new ArrayList<>();
        cpu.add(normalCpu);
        cpu.add(highCpu);

        /**
         * We clear the rules from the RulesStoreService
         */
        RulesStoreService rulesStoreService = ServiceFactory.getRulesStoreService();
        rulesStoreService.clear();

        AlertsService alertsService = ServiceFactory.getAlertsService();

        /**
         * No rules.
         */
        alertsService.reloadRules();

        /**
         * Only JVM Rule
         */
        rulesStoreService.addRule("JVM", PocRulesStoreService.DemoRules.RULE_JVM_MEM);
        alertsService.reloadRules();

        alertsService.sendEvent(normalJvm);
        alertsService.sendEvent(highJvm);
        alertsService.sendBatch(cpu);

        Thread.sleep(4000);

        /**
         * We will have 1 Alert trigged from JVM rule
         */
        Assert.assertEquals(1, alertsService.checkAlert().size());

        /*
         * JVM and CPU Rules
         */
        rulesStoreService.addRule("CPU", PocRulesStoreService.DemoRules.RULE_CPU);
        alertsService.reloadRules();

        alertsService.sendEvent(normalJvm);
        alertsService.sendEvent(highJvm);
        alertsService.sendBatch(cpu);

        Thread.sleep(4000);

        /**
         * We will have 2 new Alerts + 1 previous Alert as we didn't clear the state.
         */
        Assert.assertEquals(3, alertsService.checkAlert().size());

        /*
         * Combined Rule
         */
        rulesStoreService.addRule("CheckState", PocRulesStoreService.DemoRules.RULE_COMBINED);
        alertsService.reloadRules();

        alertsService.sendEvent(normalJvm);
        alertsService.sendEvent(highJvm);
        alertsService.sendBatch(cpu);

        Thread.sleep(4000);

        /**
         * We will have 2 new Alerts + 3 previous Alerts as we didn't clear the state.
         */
        Assert.assertEquals(5, alertsService.checkAlert().size());
        Assert.assertEquals(1, alertsService.checkState().size());

        rulesStoreService.reset();
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
