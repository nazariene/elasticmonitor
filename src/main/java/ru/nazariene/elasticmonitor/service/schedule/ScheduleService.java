package ru.nazariene.elasticmonitor.service.schedule;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Component;

import ru.nazariene.elasticmonitor.domain.Rule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {

    private final Scheduler scheduler;

    public boolean scheduleRule(Rule rule) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("rule", rule);

        JobDetail jobDetail = JobBuilder.newJob().ofType(QuartzJobDelegateService.class)
                .storeDurably()
                .withIdentity(rule.getName())
                .withDescription(rule.getName())
                .usingJobData(jobDataMap)
                .build();

        try {
            scheduler.scheduleJob(jobDetail, TriggerBuilder.newTrigger().forJob(jobDetail)
                    .withIdentity(rule.getName())
                    .withSchedule(getRuleSchedule(rule))
                    .build());
        } catch (SchedulerException se) {
            log.error("Failed to schedule rule. {}", ExceptionUtils.getStackTrace(se));
            return false;
        }

        return true;
    }

    private ScheduleBuilder getRuleSchedule(Rule rule) {
        if (StringUtils.isNotBlank(rule.getTrigger().getCron())) {
            return cronSchedule(rule.getTrigger().getCron());
        } else {
            return simpleSchedule().repeatForever().withIntervalInSeconds(Integer.parseInt(rule.getTrigger().getInterval()));
        }
    }
}

