package ru.nazariene.elasticmonitor.service.action;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.nazariene.elasticmonitor.domain.Rule;
import ru.nazariene.elasticmonitor.domain.action.Action;
import ru.nazariene.elasticmonitor.domain.action.LogAction;
import ru.nazariene.elasticmonitor.service.MessageComposerService;

@Component
@Slf4j
@RequiredArgsConstructor
public class LogActionService implements IActionService {

    private final MessageComposerService messageComposerService;

    @Override
    public void executeAction(Action action, Rule rule, JsonNode jsonSearchResponse) {
        LogAction logAction = (LogAction) action;

        String messageTemplate = logAction.getMessage();
        String processedMessageTemplate = messageComposerService.composeMessage(messageTemplate, rule, jsonSearchResponse);

        log.error(processedMessageTemplate);
    }
}
