## Scheduling service activities

One of the most common scenarios for an OSGi service is running a scheduled task. Many tasks are deliberately put off until such time that the probability of user interference is minimal (late in the night or at weekends).  

There are several ways to schedule a task. Nowadays, the prevailing approach is to use the _Sling Scheduler API_. It leverages the scheduler config and the scheduler service.

Below we will display the sample of a service that references a [Scheduler](https://sling.apache.org/apidocs/sling9/org/apache/sling/commons/scheduler/Scheduler.html#schedule-java.lang.Object-org.apache.sling.commons.scheduler.ScheduleOptions-) instance and handles the OSGi config to "commit itself" for a periodic run.

Consider the following config object:
```java
@ObjectClassDefinition(name = "Scheduler Configuration")
public @interface SchedulerConfiguration {

	@AttributeDefinition(name = "Enabled", type = AttributeType.BOOLEAN)
	boolean enabled() default true;
	
	@AttributeDefinition(name = "Cron Expression")
	String expression() default "0 15 5 ? * MON *";
}
```

In comprises two properties: whether the scheduler is enabled and the frequency of runs. The latter is defined by a Cron expression (the default value here is "Monday morning 5:15"). You can find a constructor of Cron expressions for various frequencies [here](https://www.freeformatter.com/cron-expression-generator-quartz.html).

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

The _Scheduler_ we use works with two types of entities: a Job, or a Runnable. Sling Jobs are an interesting technology itself, but it is out of our scope. 

That is why we made our service implement `Runnable` apart from its "very own" interface. Thus, the logic of this service can be triggered in two ways. We can run it "manually" (to say, from another service or a Sling model) with the `download()` method. Else, it can be triggered "automatically" on schedule. If you don't need any "manual" calls, you can make the service implement only `Runnable`.

Upon activation or modification, this service parses the config to see whether the scheduler is enabled or not. If it is enabled, the new `ScheduleOptions` object is created. The `SCHEDULER_ID` value is needed to make sure that the same task is not submitted several times: if you schedule it again, the previous schedule is discarded.

> You can find a similar scheduler responsible for periodic downloading of new music albums in our sample project [here](../../project/core/src/main/java/com/exadel/aem/core/services/impl/NewAlbumsRetrievalScheduler.java).
