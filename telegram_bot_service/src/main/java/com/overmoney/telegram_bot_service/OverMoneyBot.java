package com.overmoney.telegram_bot_service;

import com.overmoney.telegram_bot_service.constants.Command;
import com.overmoney.telegram_bot_service.constants.InlineKeyboardCallback;
import com.overmoney.telegram_bot_service.mapper.ChatMemberMapper;
import com.overmoney.telegram_bot_service.mapper.TransactionMapper;
import com.overmoney.telegram_bot_service.model.MessageTelegram;
import com.overmoney.telegram_bot_service.service.MergeRequestService;
import com.overmoney.telegram_bot_service.service.MessageTelegramService;
import com.overmoney.telegram_bot_service.service.OrchestratorRequestService;
import com.overmoney.telegram_bot_service.service.VoiceMessageProcessingService;
import com.overmoney.telegram_bot_service.util.InlineKeyboardMarkupUtil;
import com.override.dto.AccountDataDTO;
import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import com.override.dto.constants.StatusMailing;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    @Value("${orchestrator.host}")
    private String orchestratorHost;
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

    @Autowired
    private MessageTelegramService messageTelegramService;
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
            "Для корректной регистрации аккаунта убедитесь, что на момент добавления в чат бота" +
                    " в чате с ботом только вы. После переноса данных можете добавлять других пользователей";
    private final String INVALID_TRANSACTION_TO_DELETE = "Некорректная транзакция для удаления";
    private final String SUCCESSFUL_DELETION_TRANSACTION = "Эта запись успешно удалена!";
    private final String COMMAND_TO_DELETE_TRANSACTION = "удалить";

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
            Message receivedMessage = update.getMessage();
            Long chatId = receivedMessage.getChatId();
            if (receivedMessage.getLeftChatMember() != null) {
                User user = receivedMessage.getLeftChatMember();
                orchestratorRequestService.removeChatMemberFromAccount(chatMemberMapper.mapUserToChatMemberDTO(chatId, user));
            }
            if (!receivedMessage.getNewChatMembers().isEmpty()) {
                List<User> newUsers = receivedMessage.getNewChatMembers();
                HashMap<Boolean, User> usersTypes = getUsersTypes(newUsers);

                if (usersTypes.containsKey(BOT)) {
                    sendRegistrationGroupAccountInfo(chatId);
                    sendMergeRequest(chatId);
                } else {
                    orchestratorRequestService.addNewChatMembersToAccount(
                            newUsers.stream()
                                    .map(member -> chatMemberMapper.mapUserToChatMemberDTO(chatId, member))
                                    .collect(Collectors.toList()));
                }
            }
            botAnswer(receivedMessage);
        }
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            Long chatId = callbackQuery.getMessage().getChatId();
            Long userId = callbackQuery.getFrom().getId();
            Integer messageToDeleteId = mergeRequestService.getMergeRequestByChatId(chatId).getMessageId();

            if (callbackQuery.getData().equals(InlineKeyboardCallback.MERGE_CATEGORIES.getData())) {
                orchestratorRequestService.registerGroupAccountAndMergeWithCategoriesAndWithoutTransactions(
                        new AccountDataDTO(chatId, userId));
                sendMessage(chatId, MERGE_REQUEST_COMPLETED_TEXT);
            } else if (callbackQuery.getData().equals(InlineKeyboardCallback.MERGE_CATEGORIES_AND_TRANSACTIONS.getData())) {
                orchestratorRequestService.registerGroupAccountAndWithCategoriesAndTransactions(
                        new AccountDataDTO(chatId, userId));
                sendMessage(chatId, MERGE_REQUEST_COMPLETED_TEXT);
            } else if (callbackQuery.getData().equals(InlineKeyboardCallback.DEFAULT.getData())) {
                orchestratorRequestService.registerGroupAccount(new AccountDataDTO(chatId, userId));
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

    private String getReceivedMessage(Message message) {
        String receivedMessageText = BLANK_MESSAGE;
        Long userId = message.getFrom().getId();
        Long chatId = message.getChatId();
        if (message.hasVoice()) {
            log.info("user with id " + userId + " and chatId " + chatId + " sending voice");
            receivedMessageText = voiceMessageProcessingService.processVoiceMessage(message.getVoice(), userId, chatId);
            log.info("recognition result of user with id " + userId + " and chatId " + chatId + " is: " + receivedMessageText);
        } else if (message.hasText()) {
            receivedMessageText = message.getText();
        }
        return receivedMessageText;
    }

    private void botAnswer(Message receivedMessage) {
        Long chatId = receivedMessage.getChatId();
        Long userId = receivedMessage.getFrom().getId();
        String receivedMessageText = getReceivedMessage(receivedMessage);
        Message replyToMessage = receivedMessage.getReplyToMessage();
        LocalDateTime date = Instant.ofEpochMilli((long) receivedMessage.getDate() * MILLISECONDS_CONVERSION)
                .atOffset(MOSCOW_OFFSET).toLocalDateTime();
        if (receivedMessageText.equals(BLANK_MESSAGE)) {
            return;
        }
        switch (receivedMessageText.toLowerCase()) {
            case "/start":
                sendMessage(chatId, Command.START.getDescription() + orchestratorHost);
                orchestratorRequestService.registerSingleAccount(new AccountDataDTO(chatId, userId));
                break;
            case "/money":
                sendMessage(chatId, Command.MONEY.getDescription());
                break;
            case COMMAND_TO_DELETE_TRANSACTION:
                if (replyToMessage != null) {
                    deleteTransaction(replyToMessage, chatId);
                    break;
                }
            default:
                try {
                    TransactionResponseDTO transactionResponseDTO = orchestratorRequestService
                            .sendTransaction(new TransactionMessageDTO(receivedMessageText, userId, chatId, date));
                    messageTelegramService.saveMessageTelegram(new MessageTelegram(receivedMessage.getMessageId(), transactionResponseDTO.getId()));
                    sendMessage(chatId, transactionMapper.mapTransactionResponseToTelegramMessage(transactionResponseDTO));
                } catch (Exception e) {
                    log.error(e.getMessage());
                    sendMessage(chatId, TRANSACTION_MESSAGE_INVALID);
                }
                break;
        }
    }

    public StatusMailing sendMessage(Long chatId, String messageText) {
        SendMessage message = new SendMessage(chatId.toString(), messageText);
        try {
            execute(message);
            return StatusMailing.SUCCESS;
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
            return StatusMailing.ERROR;
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

    private void deleteTransaction(Message replyToMessage, Long chatId) {
        try {
            messageTelegramService.deleteTransactionById(replyToMessage.getMessageId());
            sendMessage(chatId, SUCCESSFUL_DELETION_TRANSACTION);
        } catch (Exception e) {
            log.error(e.getMessage());
            sendMessage(chatId, INVALID_TRANSACTION_TO_DELETE);
        }
    }
}