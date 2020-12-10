package ru.nazariene.elasticmonitor.service.action;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;
import ru.nazariene.elasticmonitor.domain.Rule;
import ru.nazariene.elasticmonitor.domain.action.Action;
import ru.nazariene.elasticmonitor.domain.action.TelegramAction;
import ru.nazariene.elasticmonitor.service.MessageComposerService;

@Component
@Slf4j
public class TelegramActionService extends DefaultAbsSender implements IActionService {

    private final MessageComposerService messageComposerService;

    @Value("${telegram.token}")
    private String token;

    public TelegramActionService(MessageComposerService messageComposerService) {
        super(new DefaultBotOptions());
        this.messageComposerService = messageComposerService;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void executeAction(Action action, Rule rule, JsonNode jsonSearchResponse) {
        TelegramAction telegramAction = (TelegramAction) action;

        SendMessage response = new SendMessage();
        response.setChatId(telegramAction.getChannelId());
        response.setParseMode("markdown");
        String messageTemplate = telegramAction.getMessage();
        String processedMessageTemplate = messageComposerService.composeMessage(messageTemplate, rule, jsonSearchResponse);
        response.setText(processedMessageTemplate);
        try {
            execute(response);
            log.info("Sent message \"{}\" to {}", processedMessageTemplate, telegramAction.getChannelId());
        } catch (TelegramApiException e) {
            log.error("Failed to send message \"{}\" to {} due to error: {}", processedMessageTemplate, telegramAction.getChannelId(), e.getMessage());
        }
    }
}


