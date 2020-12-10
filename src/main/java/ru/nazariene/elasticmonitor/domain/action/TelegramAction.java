package ru.nazariene.elasticmonitor.domain.action;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TelegramAction extends Action {


    private String botToken;
    private String channelId;
    private String message;

}
