package ru.nazariene.elasticmonitor.domain.action;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogAction extends Action {

    private String message;
}
