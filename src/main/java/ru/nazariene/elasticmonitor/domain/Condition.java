package ru.nazariene.elasticmonitor.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Condition {

    @JsonProperty(required = true)
    private String field;

    @JsonProperty(required = true)
    private String fieldType;

    @JsonProperty(required = true)
    private String op;

    @JsonProperty(required = true)
    private String value;
}
