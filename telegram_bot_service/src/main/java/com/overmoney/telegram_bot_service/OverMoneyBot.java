package com.overmoney.telegram_bot_service;

import com.overmoney.telegram_bot_service.constants.InlineKeyboardCallback;
import com.overmoney.telegram_bot_service.constants.Command;
import com.overmoney.telegram_bot_service.mapper.ChatMemberMapper;
import com.overmoney.telegram_bot_service.mapper.TransactionMapper;
import com.overmoney.telegram_bot_service.service.MergeRequestService;
import com.overmoney.telegram_bot_service.util.InlineKeyboardMarkupUtil;
import com.override.dto.AccountDataDTO;
import com.overmoney.telegram_bot_service.service.VoiceMessageProcessingService;
import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import com.overmoney.telegram_bot_service.service.OrchestratorRequestService;
import com.override.dto.GroupAccountDataDTO;
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
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.*;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
    private ChatMemberMapper chatMemberMapper;
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
            "Данные аккаунта были перенесены.";
    private final String REGISTRATION_INFO_TEXT =
            "Для корректной регистрации аккаунта убедитесь, что на момент добавления в бота" +
                    "в чате с ботом только вы. После переноса данных можете добавлять других пользователей";
    private final String BLANK_MESSAGE = "";
    private final Boolean BOT = true;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Long chatId = update.getMessage().getChatId();
            Long userId = update.getMessage().getFrom().getId();
            String receivedMessage = getReceivedMessage(update);
            LocalDateTime date = Instant.ofEpochMilli((long) update.getMessage().getDate() * MILLISECONDS_CONVERSION)
                    .atOffset(MOSCOW_OFFSET).toLocalDateTime();

            if (!update.getMessage().getNewChatMembers().isEmpty()) {
                List<User> newUsers = update.getMessage().getNewChatMembers();
                HashMap<Boolean, User> usersTypes = getUsersTypes(newUsers);

                if (usersTypes.containsKey(BOT)) {
                    newUsers.remove(usersTypes.get(BOT));
                    sendRegistrationGroupAccountInfo(chatId);
                    sendMergeRequest(chatId);
                } else {
                    orchestratorRequestService.addNewChatMembersToAccount(
                            newUsers.stream()
                                    .map(member -> chatMemberMapper.mapUserToChatMemberDTO(chatId, member))
                                    .collect(Collectors.toList()));
                }
            }

            if (!receivedMessage.equals(BLANK_MESSAGE)) {
                botAnswer(receivedMessage, chatId, userId, date);
            }
        }
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            Long chatId = callbackQuery.getMessage().getChatId();
            Long userId = callbackQuery.getFrom().getId();
            Integer messageToDeleteId = mergeRequestService.getMergeRequestByChatId(chatId).getMessageId();

            if (callbackQuery.getData().equals(InlineKeyboardCallback.MERGE_CATEGORIES.getData())) {
                orchestratorRequestService.registerGroupAccountAndMergeWithCategoriesAndWithoutTransactions(
                        new GroupAccountDataDTO(chatId, userId));
                sendMessage(chatId, MERGE_REQUEST_COMPLETED_TEXT);
            } else if (callbackQuery.getData().equals(InlineKeyboardCallback.MERGE_CATEGORIES_AND_TRANSACTIONS.getData())) {
                orchestratorRequestService.registerGroupAccountAndWithCategoriesAndTransactions(
                        new GroupAccountDataDTO(chatId, userId));
                sendMessage(chatId, MERGE_REQUEST_COMPLETED_TEXT);
            } else if (callbackQuery.getData().equals(InlineKeyboardCallback.DEFAULT.getData())) {
                orchestratorRequestService.registerGroupAccount(new GroupAccountDataDTO(chatId, userId));
            }

            mergeRequestService.updateMergeRequestCompletionByChatId(chatId);
            deleteMessageMarkup(messageToDeleteId, chatId);
            sendMessage(chatId, MERGE_REQUEST_COMPLETED_DEFAULT_TEXT);
        }
    }

    private HashMap<Boolean, User> getUsersTypes(List<User> newUsers) {
        HashMap<Boolean, User> userTypes = new HashMap<>();
        newUsers.forEach(user -> {
            if (user.getIsBot()) {
                userTypes.put(BOT, user);
            } else {
                userTypes.put(!BOT, user);
            }
        });
        return userTypes;
    }

    private void sendRegistrationGroupAccountInfo(Long chatId) throws TelegramApiException {
        SendMessage message = new SendMessage(chatId.toString(), REGISTRATION_INFO_TEXT);
        execute(message);
    }

    private void sendMergeRequest(Long chatId) throws TelegramApiException {
        SendMessage message = new SendMessage(chatId.toString(), MERGE_REQUEST_TEXT);
        message.setReplyMarkup(inlineKeyboardMarkupUtil.generateMergeRequestMarkup());
        Message mergeRequestMessage = execute(message);
        mergeRequestService.saveMergeRequestMessage(mergeRequestMessage);
    }

    private String getReceivedMessage(Update update) {
        String receivedMessage = BLANK_MESSAGE;
        if (update.getMessage().hasVoice()) {
            receivedMessage = voiceMessageProcessingService.processVoiceMessage(update.getMessage().getVoice());
        } else if (update.getMessage().hasText()) {
            receivedMessage = update.getMessage().getText();
        }
        return receivedMessage;
    }

    private void botAnswer(String receivedMessage, Long chatId, Long userId, LocalDateTime date) {
        switch (receivedMessage) {
            case "/start":
                sendMessage(chatId, Command.START.getDescription());
                orchestratorRequestService.registerSingleAccount(new AccountDataDTO(chatId, userId));
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
    private void deleteMessageMarkup(Integer messageId, Long chatId) {
        EditMessageReplyMarkup message = new EditMessageReplyMarkup();
        message.setChatId(chatId.toString());
        message.setMessageId(messageId);
        message.setReplyMarkup(null);
        execute(message);
    }
}