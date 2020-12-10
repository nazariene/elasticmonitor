package ru.nazariene.elasticmonitor.configuration.properties;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "elasticsearch")
@Component
@Validated
@Getter
@Setter
public class ElasticsearchProperties {

    private String username;

    private String password;

    @NotBlank
    private String host;

    @Min(1)
    private int port;
}
