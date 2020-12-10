package ru.nazariene.elasticmonitor.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Transform {

    private String index;
    private String query;
}
