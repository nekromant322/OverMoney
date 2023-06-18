package com.overmoney.telegram_bot_service;

import com.overmoney.telegram_bot_service.constants.Command;
import com.overmoney.telegram_bot_service.mapper.TransactionMapper;
import com.override.dto.AccountDataDTO;
import com.overmoney.telegram_bot_service.service.VoiceMessageProcessingService;
import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import com.overmoney.telegram_bot_service.service.OrchestratorRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.*;

@Component
@Slf4j
public class OverMoneyBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;
    @Autowired
    private OrchestratorRequestService orchestratorRequestService;
    @Autowired
    private TransactionMapper transactionMapper;
    @Autowired
    private VoiceMessageProcessingService voiceMessageProcessingService;
    private final String TRANSACTION_MESSAGE_INVALID = "Мы не смогли распознать ваше сообщение. " +
            "Убедитесь, что сумма и товар указаны верно и попробуйте еще раз :)";
    private final Integer MILLISECONDS_CONVERSION = 1000;
    private final ZoneOffset MOSCOW_OFFSET = ZoneOffset.of("+03:00");

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

        LocalDateTime date = Instant.ofEpochMilli((long) update.getMessage().getDate() * MILLISECONDS_CONVERSION)
                .atOffset(MOSCOW_OFFSET).toLocalDateTime();
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();

        if (update.getMessage().hasText()) {
            String receivedMessage = update.getMessage().getText();
            botAnswer(receivedMessage, chatId, userId, date);
        }

        if (update.getMessage().hasVoice()) {
            String receivedMessage = voiceMessageProcessingService.processVoiceMessage(update.getMessage().getVoice());
            botAnswer(receivedMessage, chatId, userId, date);
        }
    }

    private void botAnswer(String receivedMessage, Long chatId, Long userId, LocalDateTime date) {
        switch (receivedMessage) {
            case "/start":
                sendMessage(chatId, Command.START.getDescription());
                orchestratorRequestService.registerAccount(new AccountDataDTO(chatId, userId));
                break;
            case "/money":
                sendMessage(chatId, Command.MONEY.getDescription());
                break;
            default:
                try {
                    TransactionResponseDTO transactionResponseDTO = orchestratorRequestService.sendTransaction(new TransactionMessageDTO(receivedMessage, userId, chatId, date));
                    sendMessage(chatId, transactionMapper.mapTransactionResponseToTelegramMessage(transactionResponseDTO));
                } catch (Exception e) {
                    sendMessage(chatId, TRANSACTION_MESSAGE_INVALID);
                }
                break;
        }
    }

    public void sendMessage(Long chatId, String messageText) {
        SendMessage message = new SendMessage(chatId.toString(), messageText);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
