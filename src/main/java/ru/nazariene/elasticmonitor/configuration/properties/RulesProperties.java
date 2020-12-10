package ru.nazariene.elasticmonitor.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "rules")
@Component
@Validated
@Getter
@Setter
public class RulesProperties {

    private String location;
}
