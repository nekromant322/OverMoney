package com.overmoney.telegram_bot_service;

import com.overmoney.telegram_bot_service.constants.Command;
import com.overmoney.telegram_bot_service.mapper.TransactionMapper;
import com.override.dto.AccountDataDTO;
import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import com.overmoney.telegram_bot_service.service.OrchestratorRequestService;
import com.overmoney.telegram_bot_service.service.TelegramBotApiRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class OverMoneyBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.voice.max_length}")
    private int voiceMessageMaxLength;
    @Autowired
    private OrchestratorRequestService orchestratorRequestService;
    @Autowired
    private TelegramBotApiRequestService telegramBotApiRequestService;
    @Autowired
    private TransactionMapper transactionMapper;
    private final String VOICE_MESSAGE_TOO_LONG = "К сожалению, мы не можем распознать голосовое сообщение длиннее "
            +  voiceMessageMaxLength + " секунд - попробуйте разбить его на части поменьше :^)";
    private final String TRANSACTION_MESSAGE_INVALID = "Мы не смогли распознать ваше сообщение. " +
            "Убедитесь, что сумма и товар указаны верно и попробуйте еще раз :)";

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        String username = update.getMessage().getFrom().getUserName();

        if (update.getMessage().hasText()) {
            String receivedMessage = update.getMessage().getText();
            botAnswer(receivedMessage, chatId, username);
        }

        if (update.getMessage().hasVoice()) {
            if (update.getMessage().getVoice().getDuration() > voiceMessageMaxLength) {
                sendMessage(chatId, VOICE_MESSAGE_TOO_LONG);
            } else {
                byte[] voiceMessage = telegramBotApiRequestService.getVoiceMessageBytes(update.getMessage().getVoice().getFileId());
                orchestratorRequestService.sendVoiceMessage(voiceMessage);
            }
        }
    }

    private void botAnswer(String receivedMessage, Long chatId, String username) {
        switch (receivedMessage) {
            case "/start":
                sendMessage(chatId, Command.START.getDescription());
                orchestratorRequestService.registerOverMoneyAccount(new AccountDataDTO(chatId, username));
                break;
            case "/money":
                sendMessage(chatId, Command.MONEY.getDescription());
                break;
            default:
                try {
                    TransactionResponseDTO transactionResponseDTO = orchestratorRequestService.sendTransaction(new TransactionMessageDTO(receivedMessage, username, chatId));
                    sendMessage(chatId, transactionMapper.mapTransactionResponseToTelegramMessage(transactionResponseDTO));
                } catch (Exception e) {
                    sendMessage(chatId, TRANSACTION_MESSAGE_INVALID);
                }
                break;
        }
    }

    private void sendMessage(Long chatId, String messageText) {
        SendMessage message = new SendMessage(chatId.toString(), messageText);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
