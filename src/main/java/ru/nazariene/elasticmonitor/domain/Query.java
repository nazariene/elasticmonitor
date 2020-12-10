package ru.nazariene.elasticmonitor.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Query {

    private String index;

    @JsonProperty(required = true)
    private String value;

}
