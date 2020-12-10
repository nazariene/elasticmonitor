package ru.nazariene.elasticmonitor.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ru.nazariene.elasticmonitor.domain.action.Action;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class Rule {

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true)
    private Trigger trigger;

    @JsonProperty(required = true)
    private Query query;

    @JsonProperty(required = true)
    private Condition condition;

    private Transform transform;

    private List<Action> action;
}
