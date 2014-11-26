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
    public void dummyTest() throws Exception {
        AlertsService alertsService = AlertsFactory.getAlertsService();

        for (int i = 0; i < 3; i++) {
            Event e = new Event("E" + i, Math.random(), System.currentTimeMillis());
            alertsService.sendEvent(e);
        }

        Thread.sleep(5 * 1000);

        Assert.assertEquals(3, alertsService.checkState().size());
    }

}
