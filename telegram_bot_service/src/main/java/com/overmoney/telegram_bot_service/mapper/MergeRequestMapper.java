package com.overmoney.telegram_bot_service.mapper;

import com.overmoney.telegram_bot_service.model.MergeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@Slf4j
public class MergeRequestMapper {
    private final Boolean DEFAULT_COMPLETION = false;
    private final Integer MILLISECONDS_CONVERSION = 1000;
    private final ZoneOffset MOSCOW_OFFSET = ZoneOffset.of("+03:00");

    public MergeRequest mapMergeRequestMessageToMergeRequest(Message message) {
        LocalDateTime date = Instant.ofEpochMilli((long) message.getDate() * MILLISECONDS_CONVERSION)
                .atOffset(MOSCOW_OFFSET).toLocalDateTime();

        return MergeRequest.builder()
                .chatId(message.getChatId())
                .messageId(message.getMessageId())
                .completed(DEFAULT_COMPLETION)
                .date(date)
                .build();
    }
}