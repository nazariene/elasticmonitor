package ru.nazariene.elasticmonitor.service.action;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import ru.nazariene.elasticmonitor.domain.Rule;
import ru.nazariene.elasticmonitor.domain.action.Action;
import ru.nazariene.elasticmonitor.domain.action.EmailAction;
import ru.nazariene.elasticmonitor.service.MessageComposerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailActionService implements IActionService {

    private final MessageComposerService messageComposerService;

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void executeAction(Action action, Rule rule, JsonNode jsonSearchResponse) {
        log.info("Sending email");
        EmailAction emailAction = (EmailAction) action;
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setFrom("pcm-bot@t-systems.com");
            messageHelper.setTo(emailAction.getTo());
            messageHelper.setSubject(emailAction.getSubject());

            messageHelper.setText(messageComposerService.composeMessage(emailAction.getBody(), rule, jsonSearchResponse), true);
        };

        try {
            mailSender.send(messagePreparator);
            log.info("Sent message to {}", emailAction.getTo());
        } catch (RuntimeException me) {
            log.error("Failed to send message to {} due to error: {}", emailAction.getTo(), me.getMessage());
        }
    }
}