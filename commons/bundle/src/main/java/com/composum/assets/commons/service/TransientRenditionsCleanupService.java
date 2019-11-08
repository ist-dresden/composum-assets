package com.composum.assets.commons.service;

import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Periodically cleans up transient renditions. Since it is hard to find out whether a rendition is going to be used
 * ever again, we delete the transient renditions after a while and let them be recreated if they are used again. We
 * keep them around for a random time to avoid having to recreate everything at the same time.
 *
 * @see "https://sling.apache.org/documentation/bundles/scheduler-service-commons-scheduler.html"
 * @see "http://www.docjar.com/docs/api/org/quartz/CronTrigger.html"
 */
@Component(
        service = Runnable.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Assets Cleanup Service",
                // Scheduler.PROPERTY_SCHEDULER_EXPRESSION + "=0 * * * * ?",
                Scheduler.PROPERTY_SCHEDULER_CONCURRENT + ":Boolean=false",
                // Scheduler.PROPERTY_SCHEDULER_PERIOD + ":Long=10",
                Scheduler.PROPERTY_SCHEDULER_RUN_ON + "=" + Scheduler.VALUE_RUN_ON_SINGLE
        },
        immediate = true
)
public class TransientRenditionsCleanupService implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(TransientRenditionsCleanupService.class);

    @Override
    public void run() {
        LOG.info("TransientRenditionsCleanupService.run");
    }

    @Activate
    public void activate() {
        LOG.info("activated");
    }
}
