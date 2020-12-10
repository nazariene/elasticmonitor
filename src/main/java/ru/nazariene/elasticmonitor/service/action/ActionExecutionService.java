package ru.nazariene.elasticmonitor.service.action;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import ru.nazariene.elasticmonitor.domain.Rule;
import ru.nazariene.elasticmonitor.domain.action.Action;
import ru.nazariene.elasticmonitor.domain.action.EmailAction;
import ru.nazariene.elasticmonitor.domain.action.LogAction;
import ru.nazariene.elasticmonitor.domain.action.TelegramAction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActionExecutionService {

    private final EmailActionService emailActionService;
    private final TelegramActionService telegramActionService;
    private final LogActionService logActionService;

    public void executeActions(Rule rule, JsonNode searchResponse) {
        rule.getAction().forEach(it -> executeAction(it, rule, searchResponse));
    }

    private void executeAction(Action action, Rule rule, JsonNode searchResponse) {
        if (action instanceof EmailAction) {
            emailActionService.executeAction(action, rule, searchResponse);
        } else if (action instanceof TelegramAction) {
            telegramActionService.executeAction(action, rule, searchResponse);
        } else if (action instanceof LogAction) {
            logActionService.executeAction(action, rule, searchResponse);
        }
    }
}
