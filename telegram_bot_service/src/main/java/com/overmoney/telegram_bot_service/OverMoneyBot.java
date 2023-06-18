package com.overmoney.telegram_bot_service;

import com.overmoney.telegram_bot_service.constants.InlineKeyboardCallback;
import com.overmoney.telegram_bot_service.constants.Command;
import com.overmoney.telegram_bot_service.mapper.TransactionMapper;
import com.overmoney.telegram_bot_service.service.MergeRequestService;
import com.overmoney.telegram_bot_service.util.InlineKeyboardMarkupUtil;
import com.override.dto.AccountDataDTO;
import com.overmoney.telegram_bot_service.service.VoiceMessageProcessingService;
import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import com.overmoney.telegram_bot_service.service.OrchestratorRequestService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
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
    @Autowired
    private MergeRequestService mergeRequestService;
    @Autowired
    private InlineKeyboardMarkupUtil inlineKeyboardMarkupUtil;
    private final String TRANSACTION_MESSAGE_INVALID = "Мы не смогли распознать ваше сообщение. " +
            "Убедитесь, что сумма и товар указаны верно и попробуйте еще раз :)";
    private final Integer MILLISECONDS_CONVERSION = 1000;
    private final ZoneOffset MOSCOW_OFFSET = ZoneOffset.of("+03:00");
    private final String MERGE_REQUEST_TEXT =
            "Привет, ты добавил меня в груповой чат, теперь я буду отслеживать " +
                    "транзакции всех пользователей в этом чате.\n\n" +
                    "Хочешь перенести данные о своих финансах и использовать их совместно?";
    private final String MERGE_REQUEST_COMPLETED_DEFAULT_TEXT =
            "Удачного совместного использования!";
    private final String MERGE_REQUEST_COMPLETED_TEXT =
            "Данные аккаунта были перенесены, удачного совместного использования!";

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
        if (update.hasMessage()) {
            String receivedMessage = "";
            Long chatId = update.getMessage().getChatId();
            Long userId = update.getMessage().getFrom().getId();
            LocalDateTime date = Instant.ofEpochMilli((long) update.getMessage().getDate() * MILLISECONDS_CONVERSION)
                    .atOffset(MOSCOW_OFFSET).toLocalDateTime();

            if (update.getMessage().hasVoice()) {
                receivedMessage = voiceMessageProcessingService.processVoiceMessage(update.getMessage().getVoice());
            }

            if (update.getMessage().hasText()) {
                receivedMessage = update.getMessage().getText();
            }

            botAnswer(receivedMessage, chatId, userId, date);
        }
        if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            Integer messageToDeleteId = mergeRequestService.getMergeRequestByChatId(chatId).getMessageId();
            CallbackQuery callbackQuery = update.getCallbackQuery();

            if (callbackQuery.getData().equals(InlineKeyboardCallback.DEFAULT.getData())) {
                mergeRequestService.updateMergeRequestCompletionByChatId(chatId);
                deleteMessageMarkup(messageToDeleteId, chatId);
                sendMessage(chatId, MERGE_REQUEST_COMPLETED_DEFAULT_TEXT);
            }
            if (callbackQuery.getData().equals(InlineKeyboardCallback.MERGE_CATEGORIES.getData())) {
                orchestratorRequestService.mergeWithCategoriesAndWithoutTransactions(callbackQuery.getFrom().getId());
                mergeRequestService.updateMergeRequestCompletionByChatId(chatId);
                deleteMessageMarkup(messageToDeleteId, chatId);
                sendMessage(chatId, MERGE_REQUEST_COMPLETED_TEXT);
            }
            if (callbackQuery.getData().equals(InlineKeyboardCallback.MERGE_CATEGORIES_AND_TRANSACTIONS.getData())) {
                orchestratorRequestService.mergeWithCategoryAndTransactions(callbackQuery.getFrom().getId());
                mergeRequestService.updateMergeRequestCompletionByChatId(chatId);
                deleteMessageMarkup(messageToDeleteId, chatId);
                sendMessage(chatId, MERGE_REQUEST_COMPLETED_TEXT);
            }
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

    @SneakyThrows
    public void sendMergeRequest(Long userId) {
        SendMessage message = new SendMessage(userId.toString(), MERGE_REQUEST_TEXT);
        message.setReplyMarkup(inlineKeyboardMarkupUtil.generateMergeRequestMarkup());
        Message mergeRequestMessage = execute(message);
        mergeRequestService.saveMergeRequestMessage(mergeRequestMessage);
    }

    @SneakyThrows
    private void deleteMessageMarkup(Integer messageId, Long chatId) {
        EditMessageReplyMarkup message = new EditMessageReplyMarkup();
        message.setChatId(chatId.toString());
        message.setMessageId(messageId);
        message.setReplyMarkup(null);
        execute(message);
    }
}