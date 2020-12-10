package ru.nazariene.elasticmonitor.service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import ru.nazariene.elasticmonitor.configuration.properties.RulesProperties;
import ru.nazariene.elasticmonitor.domain.Rule;
import ru.nazariene.elasticmonitor.service.schedule.ScheduleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class InitService {

    private final RulesProperties rulesProperties;

    private final ScheduleService scheduleService;

    @EventListener(ApplicationReadyEvent.class)
    public void doInit() throws IOException {
        var rule = loadRules();

        log.info("Got rules: {}", rule);
        rule.forEach(scheduleService::scheduleRule);
    }

    public List<Rule> loadRules() {
        if (StringUtils.isBlank(rulesProperties.getLocation())) {
            throw new RuntimeException("rules.location MUST be set!");
        }

        var ruleLocation = new File(rulesProperties.getLocation());

        if (ruleLocation.isDirectory()) {
            return Arrays.stream(ruleLocation.listFiles())
                    .filter(it -> it.getName().endsWith(".yml") || it.getName().endsWith(".yaml"))
                    .map(this::parseRule)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            return List.of(Objects.requireNonNull(parseRule(ruleLocation)));
        }
    }

    private Rule parseRule(File file) {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            return objectMapper.readValue(file, Rule.class);
        } catch (IOException ioe) {
            log.error("Failed to parse rule from {} because {}", file.getName(), ExceptionUtils.getStackTrace(ioe));
            return null;
        }
    }
}
