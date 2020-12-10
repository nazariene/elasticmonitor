package ru.nazariene.elasticmonitor.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.TagType;
import com.github.jknack.handlebars.Template;

import ru.nazariene.elasticmonitor.domain.Rule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageComposerService {

    private final Environment environment;

    public String composeMessage(String messageTemplate, Rule rule, JsonNode searchResponse) {
        Handlebars handlebars = new Handlebars();
        try {
            Template template = handlebars.compileInline(messageTemplate);
            List<String> variablesToSubstitute = template.collect(TagType.VAR, TagType.TRIPLE_VAR, TagType.SECTION);
            var templateData = populateTemplateVariables(rule, variablesToSubstitute, searchResponse);
            return template.apply(templateData);
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to compose message! Check logs";
        }
    }

    private Map<String, Object> populateTemplateVariables(Rule rule, List<String> variables, JsonNode searchResponse) {
        var result = new HashMap<String, Object>();
        var searchVariables = populateSearchVariables(variables, searchResponse);
        var envVariables = populateEnvironmentVariables(variables);

        result.put("query", rule.getQuery());
        result.put("condition", rule.getCondition());
        result.put("transform", rule.getTransform());
        result.put("trigger", rule.getTrigger());
        result.put("action", rule.getAction());

        result.putAll(searchVariables);
        result.putAll(envVariables);

        return result;
    }

    private Map<String, Object> populateSearchVariables(List<String> variables, JsonNode searchResponse) {
        var searchVariables = variables.stream()
                .filter(it -> it.startsWith("[search:"))
                .map(it -> it.replaceAll("\\[", "").replaceAll("]", ""))
                .collect(Collectors.toMap(it -> it, it -> (Object) searchResponse.at(it.substring(7))));

        return searchVariables;
    }

    private Map<String, Object> populateEnvironmentVariables(List<String> variables) {
        return variables.stream()
                .filter(it -> it.startsWith("[env:"))
                .map(it -> it.replaceAll("\\[", "").replaceAll("]", ""))
                .collect(Collectors.toMap(it -> it, it -> (Object) environment.getProperty(it.substring(4))));
    }
}
