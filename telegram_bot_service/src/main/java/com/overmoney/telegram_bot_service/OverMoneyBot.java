package com.overmoney.telegram_bot_service;

import com.overmoney.telegram_bot_service.constants.Command;
import com.overmoney.telegram_bot_service.mapper.TransactionMapper;
import com.overmoney.telegram_bot_service.model.TransactionDTO;
import com.overmoney.telegram_bot_service.model.TransactionResponseDTO;
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

    private final String MESSAGE_INVALID = "Мы не смогли распознать ваше сообщение. Убедитесь, что сумма и товар указаны верно и попробуйте еще раз :)";

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    @Autowired
    OrchestratorRequestService orchestratorRequestService;

    @Autowired
    TelegramBotApiRequestService telegramBotApiRequestService;

    @Autowired
    TransactionMapper transactionMapper;

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
        String chatId = update.getMessage().getChatId().toString();
        String username = update.getMessage().getFrom().getUserName();

        if (update.getMessage().hasText()) {
            String receivedMessage = update.getMessage().getText();
            botAnswer(receivedMessage, chatId, username);
        }

        if (update.getMessage().hasVoice()) {
            byte[] voiceMessage = telegramBotApiRequestService.getVoiceMessageBytes(update.getMessage().getVoice().getFileId());
            orchestratorRequestService.sendVoiceMessage(voiceMessage);
        }
    }

    private void botAnswer(String receivedMessage, String chatId, String username) {
        switch (receivedMessage) {
            case "/start":
                sendMessage(chatId, Command.START.getDescription());
                break;
            case "/money":
                sendMessage(chatId, Command.MONEY.getDescription());
                break;
            default:
                try {
                    TransactionResponseDTO transactionResponseDTO = orchestratorRequestService.sendTransaction(new TransactionDTO(receivedMessage, username));
                    sendMessage(chatId, transactionMapper.mapTransactionResponseToTelegramMessage(transactionResponseDTO));
                } catch (Exception e) {
                    sendMessage(chatId, MESSAGE_INVALID);
                }
                break;
        }
    }

    private void sendMessage(String chatId, String messageText) {
        SendMessage message = new SendMessage(chatId, messageText);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
