package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.OverMoneyBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Voice;

@Service
public class VoiceMessageProcessingService {

    @Value("${bot.voice.max_length}")
    private int voiceMessageMaxLength;
    @Autowired
    private OverMoneyBot overMoneyBot;
    @Autowired
    private TelegramBotApiRequestService telegramBotApiRequestService;
    @Autowired
    private OrchestratorRequestService orchestratorRequestService;
    private final String VOICE_MESSAGE_TOO_LONG = "К сожалению, мы не можем распознать голосовое сообщение длиннее "
            +  voiceMessageMaxLength + " секунд - попробуйте разбить его на части поменьше :^)";

    public void processVoiceMessage(Voice voiceMessage, Long chatId) {
        if (voiceMessage.getDuration() > voiceMessageMaxLength) {
            overMoneyBot.sendMessage(chatId, VOICE_MESSAGE_TOO_LONG);
        } else {
            byte[] voiceMessageBytes = telegramBotApiRequestService.getVoiceMessageBytes(voiceMessage.getFileId());
            orchestratorRequestService.sendVoiceMessage(voiceMessageBytes);
        }
    }
}
