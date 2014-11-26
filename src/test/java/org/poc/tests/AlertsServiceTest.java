package org.poc.tests;

import org.junit.Assert;
import org.junit.Test;
import org.poc.api.AlertsFactory;
import org.poc.api.AlertsService;
import org.poc.api.Event;

/**
 * Basic tests for AlertsService
 */
public class AlertsServiceTest {

    @Test
    public void oneAlertTest() throws Exception {
        AlertsService alertsService = AlertsFactory.getAlertsService();

        Event normalJvm = new Event("JVM", 5d, System.currentTimeMillis());
        Event highJvm = new Event("JVM", 12d, System.currentTimeMillis());

        alertsService.sendEvent(normalJvm);
        alertsService.sendEvent(highJvm);

        Thread.sleep(4000);

        Assert.assertEquals(1, alertsService.checkAlert().size());
    }

}
