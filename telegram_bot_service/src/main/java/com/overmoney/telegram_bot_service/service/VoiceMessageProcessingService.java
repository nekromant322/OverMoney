package com.overmoney.telegram_bot_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Voice;

@Service
public class VoiceMessageProcessingService {

    @Value("${bot.voice.max_length}")
    private int voiceMessageMaxLength;
    @Autowired
    private TelegramBotApiRequestService telegramBotApiRequestService;
    @Autowired
    private OrchestratorRequestService orchestratorRequestService;
    private final String VOICE_MESSAGE_TOO_LONG = "К сожалению, мы не можем распознать голосовое сообщение " +
            "длиннее %d секунд - попробуйте разбить его на части поменьше :^)";
    private final String VOICE_MESSAGE_PROCESSING = "Обрабатываем ваше сообщение...";

    public String processVoiceMessage(Voice voiceMessage) {
        if (voiceMessage.getDuration() > voiceMessageMaxLength) {
            return String.format(VOICE_MESSAGE_TOO_LONG, voiceMessageMaxLength);
        }
        byte[] voiceMessageBytes = telegramBotApiRequestService.getVoiceMessageBytes(voiceMessage.getFileId());
        orchestratorRequestService.sendVoiceMessage(voiceMessageBytes);
        return VOICE_MESSAGE_PROCESSING;
    }
}
