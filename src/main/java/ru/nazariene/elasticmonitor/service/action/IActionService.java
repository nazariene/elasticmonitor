package ru.nazariene.elasticmonitor.service.action;

import com.fasterxml.jackson.databind.JsonNode;

import ru.nazariene.elasticmonitor.domain.Rule;
import ru.nazariene.elasticmonitor.domain.action.Action;

public interface IActionService {

    void executeAction(Action action, Rule rule, JsonNode jsonSearchResponse);
}
