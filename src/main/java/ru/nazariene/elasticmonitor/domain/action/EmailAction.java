package ru.nazariene.elasticmonitor.domain.action;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailAction extends Action {

    private String to;
    private String subject;
    private String body;

}
