## Scheduling service activities

One of the most common scenarios for an OSGi service is running a scheduled task. Such a task does not require an active user. Moreover, many tasks are deliberately postponed until such time that a user is unlikely to interfere (late in the night or at weekends).  

There are several ways to schedule a task. Nowadays, the most common approach is to use the _Sling Scheduler API_. It leverages the scheduler config and the scheduler service.

Below we will display the sample of a service that references a [Scheduler](https://sling.apache.org/apidocs/sling9/org/apache/sling/commons/scheduler/Scheduler.html#schedule-java.lang.Object-org.apache.sling.commons.scheduler.ScheduleOptions-) instance and handles the OSGi config to "commit itself" for a periodic run.

Consider the following config object:
```java
@ObjectClassDefinition(name = "Scheduler Configuration")
public @interface SchedulerConfiguration {

	@AttributeDefinition(name = "Enabled", type = AttributeType.BOOLEAN)
	boolean enabled() default true;
	
	@AttributeDefinition(name = "Cron Expression")
	String expression() default "0 19 5 ? * MON *";
}
```

In has two properties: whether the scheduler is enabled and the frequency of runs. The latter is defined by a Cron expression (the default value here is "Monday morning 5:19"). You can find a constructor of Cron expressions for various frequencies [here](https://www.freeformatter.com/cron-expression-generator-quartz.html).

Now look at the service that consumes the config:
```java
@Component(service = DownloaderService)
public class PeriodicDownloaderImpl implements DownloaderService, Runnable {
    
    private static final String SCHEDULER_ID = PeriodicDownloaderImpl.class.getName() + "_scheduler";
    
    @Reference
    private Scheduler scheduler;
    
    @Override 
    public void download() {
        run();
    }
    
    @Override 
    public void run() {
        // The downloading logic goes here
    }
    
    @Activate
    @Modified
    private void doActivate(SchedulerConfiguration config) {
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
    
    private void addScheduler(SchedulerConfiguration config) {
        ScheduleOptions scheduleOptions = scheduler.EXPR(config.expression());
        scheduleOptions.name(SCHEDULER_ID);
        scheduleOptions.canRunConcurrently(false);
        scheduler.schedule(this, scheduleOptions);
    }
    
    private void removeScheduler() {
        scheduler.unschedule(SCHEDULER_ID);
    }
}

// ...

public interface DownloaderService {
    void download();
}
```

The _Scheduler_ we use works with two types of entities: a Job, or a Runnable. 

Sling Jobs are an interesting technology itself, but it is out of our scope. That is why we made our service implement `Runnable` apart from its "very own" interface. 

The logic of this service can be triggered in two ways. We can run it "manually" (to say, from another service or a Sling model) with the `download()` method. Else, it can be triggered "automatically" on schedule. If you don't need any "manual" calls, you can make the service implement only `Runnable`.

Upon activation or modification, this service parses the config to see whether the scheduler is enabled. If yes, the new `ScheduleOptions` object is created. The `SCHEDULER_ID` value is needed to make sure that the same task is not submitted twice: if you try to schedule it again, the previous one is discarded.

> You can find a similar scheduler responsible for periodic downloading of new music albums in our sample project [here](../../../project/core/src/main/java/com/exadel/aem/core/services/impl/NewAlbumsRetrievalScheduler.java).

This is it for the current lesson. OSGi bundles and services are quite a complex technology, but no less they are powerful. It is the OSGi that makes an AEM instance as flexible and scalable as any other web server platform out there. 

We will continue by digging deeper into a particular OSGi feature - or rather a particular kind of OSGi components - the Servlets. Stay tuned for the next lesson.
 
---

[Previous part](part2.md)

[To Contents](../../../README.md)
