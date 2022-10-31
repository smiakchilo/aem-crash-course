package com.exadel.aem.core.schedulers;

import com.exadel.aem.core.services.AlbumRetriever;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Designate(ocd = NewAlbumsRetrievalScheduler.Config.class)
public class NewAlbumsRetrievalScheduler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(NewAlbumsRetrievalScheduler.class);
    private static final String SCHEDULER_ID = NewAlbumsRetrievalScheduler.class.getName() + "_scheduler";

    @Reference
    private Scheduler scheduler;

    @Reference
    private AlbumRetriever albumRetriever;

    @Override
    public void run() {
        try {
            albumRetriever.retrieveNewAlbums();
        } catch (Exception e) {
            LOG.error("Scheduled retrieval failed", e);
        }
    }

    @Activate
    @Modified
    private void doActivate(Config config) {
        if (config.enabled()) {
            addScheduler(config);
        } else {
            removeScheduler();
        }
    }

    @Deactivate
    private void doDeactivate() {
        removeScheduler();
    }

    private void addScheduler(Config config) {
        ScheduleOptions scheduleOptions = scheduler.EXPR(config.expression());
        scheduleOptions.name(SCHEDULER_ID);
        scheduleOptions.canRunConcurrently(false);
        scheduler.schedule(this, scheduleOptions);
    }

    private void removeScheduler() {
        scheduler.unschedule(SCHEDULER_ID);
    }

    @ObjectClassDefinition(name = " Sample AEM Project - Scheduler Configuration")
    public @interface Config {

        @AttributeDefinition(name = "Enabled", type = AttributeType.BOOLEAN)
        boolean enabled() default false;

        @AttributeDefinition(name = "Cron Expression")
        String expression() default "0 15 5 ? * MON *";
    }
}
