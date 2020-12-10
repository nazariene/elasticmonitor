package ru.nazariene.elasticmonitor.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Trigger {

    private String cron;

    private String interval;
}
