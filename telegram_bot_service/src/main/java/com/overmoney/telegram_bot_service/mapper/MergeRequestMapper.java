package com.overmoney.telegram_bot_service.mapper;

import com.overmoney.telegram_bot_service.model.MergeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@Slf4j
public class MergeRequestMapper {
    private final Boolean DEFAULT_COMPLETION = false;

    public MergeRequest mapMergeRequestMessageToMergeRequest(Message message) {
        log.info(String.valueOf(message.getChatId()));
        return MergeRequest.builder()
                .chatId(message.getChatId())
                .messageId(message.getMessageId())
                .completed(DEFAULT_COMPLETION)
                .build();
    }
}