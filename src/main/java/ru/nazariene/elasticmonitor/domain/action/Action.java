package ru.nazariene.elasticmonitor.domain.action;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EmailAction.class, name = "email"),
        @JsonSubTypes.Type(value = LogAction.class, name = "log"),
        @JsonSubTypes.Type(value = TelegramAction.class, name = "telegram"),
})
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Action {

    private String name;

}
