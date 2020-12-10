package ru.nazariene.elasticmonitor.service.schedule;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

import ru.nazariene.elasticmonitor.domain.Rule;
import ru.nazariene.elasticmonitor.service.RuleExecutionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class QuartzJobDelegateService implements Job {

    private final RuleExecutionService ruleExecutionService;

    @Override
    public void execute(JobExecutionContext context) {
        ruleExecutionService.executeRule((Rule) context.getJobDetail().getJobDataMap().get("rule"));
    }
}
