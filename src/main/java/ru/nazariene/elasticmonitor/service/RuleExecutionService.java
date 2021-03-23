package ru.nazariene.elasticmonitor.service;

import org.springframework.stereotype.Component;

import ru.nazariene.elasticmonitor.domain.Rule;
import ru.nazariene.elasticmonitor.service.action.ActionExecutionService;
import ru.nazariene.elasticmonitor.service.condition.ConditionVerificationService;
import ru.nazariene.elasticmonitor.service.elasticsearch.ElasticQueryRunner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class RuleExecutionService {

    private final ElasticQueryRunner elasticQueryRunner;
    private final ConditionVerificationService conditionVerificationService;
    private final ActionExecutionService actionExecutionService;

    public void executeRule(Rule rule) {
        log.info("Executing rule {}", rule.getName());

        var searchResponse = elasticQueryRunner.search(rule.getQuery());

        //Check if condition was breached
        var conditionBreached = conditionVerificationService.verifyCondition(rule, searchResponse);

        if (!conditionBreached) {
            log.info("Condition for rule {} not breached. Skipping actions.", rule.getName());
            return;
        } else {
            log.warn("Condition breached for rule {}! Executing actions", rule.getName());
        }

        //If alerts - call Transform service
        //TODO add transform

        //Execute actions
        actionExecutionService.executeActions(rule, searchResponse);
    }
}
