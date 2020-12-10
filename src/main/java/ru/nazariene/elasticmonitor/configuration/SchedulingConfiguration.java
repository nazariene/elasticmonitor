package ru.nazariene.elasticmonitor.configuration;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
//@EnableScheduling
public class SchedulingConfiguration {

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factory)
            throws SchedulerException {
        Scheduler scheduler = factory.getScheduler();

        scheduler.start();
        return scheduler;
    }
}
