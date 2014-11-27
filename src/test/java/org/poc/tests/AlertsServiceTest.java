package org.poc.tests;

import org.junit.Assert;
import org.junit.Test;
import org.poc.api.AlertsFactory;
import org.poc.api.AlertsService;
import org.poc.api.Event;
import org.poc.api.NotificationTask;
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
        AlertsService alertsService = AlertsFactory.getAlertsService();

        SnmpNotification snmpTrapJvm = new SnmpNotification("SNMP-Trap-JVM");
        SnmpNotification snmpTrapCpu = new SnmpNotification("SNMP-Trap-CPU");
        EmailNotification emailAdmin = new EmailNotification("admin@email.com");

        alertsService.register(snmpTrapJvm);
        alertsService.register(snmpTrapCpu);
        alertsService.register(emailAdmin);

        Event normalJvm = new Event("JVM", 5d, System.currentTimeMillis());
        Event highJvm = new Event("JVM", 12d, System.currentTimeMillis());

        Event normalCpu = new Event("CPU", 50d, System.currentTimeMillis());
        Event highCpu = new Event("CPU", 95d, System.currentTimeMillis());

        Collection<Event> cpu = new ArrayList<>();
        cpu.add(normalCpu);
        cpu.add(highCpu);

        alertsService.sendEvent(normalJvm);
        alertsService.sendEvent(highJvm);
        alertsService.sendBatch(cpu);

        Thread.sleep(4000);

        Assert.assertEquals(2, alertsService.checkAlert().size());
        Assert.assertEquals(1, alertsService.checkState().size());

        Assert.assertTrue(snmpTrapJvm.isNotified());
        Assert.assertTrue(snmpTrapCpu.isNotified());
        Assert.assertTrue(emailAdmin.isNotified());

        alertsService.finish();
    }

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
