package com.overmoney.telegram_bot_service;

import com.overmoney.telegram_bot_service.constants.Command;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class OverMoneyBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

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

        if (update.getMessage().hasText()) {
            String receivedMessage = update.getMessage().getText();
            botAnswer(receivedMessage, chatId);
        }
    }

    private void botAnswer(String receivedMessage, String chatId) {
        switch (receivedMessage) {
            case "/start":
                sendMessage(chatId, Command.START.getDescription());
                break;
            case "/money":
                sendMessage(chatId, Command.MONEY.getDescription());
                break;
            default: break;
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
