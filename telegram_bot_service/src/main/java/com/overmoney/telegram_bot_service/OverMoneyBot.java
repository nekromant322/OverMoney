package com.overmoney.telegram_bot_service;

import com.overmoney.telegram_bot_service.commands.OverMoneyCommand;
import com.overmoney.telegram_bot_service.constants.InlineKeyboardCallback;
import com.overmoney.telegram_bot_service.exception.VoiceProcessingException;
import com.overmoney.telegram_bot_service.kafka.service.KafkaProducerService;
import com.overmoney.telegram_bot_service.mapper.ChatMemberMapper;
import com.overmoney.telegram_bot_service.mapper.TransactionMapper;
import com.overmoney.telegram_bot_service.model.TelegramMessage;
import com.overmoney.telegram_bot_service.service.*;
import com.overmoney.telegram_bot_service.util.InlineKeyboardMarkupUtil;
import com.override.dto.AccountDataDTO;
import com.override.dto.TransactionDTO;
import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import com.override.dto.constants.StatusMailing;
import feign.FeignException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.overmoney.telegram_bot_service.constants.MessageConstants.*;

@Component
@Slf4j
public class OverMoneyBot extends TelegramLongPollingCommandBot {
    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;
    @Value("${service.transaction.processing}")
    private String switcher;
    @Value("${orchestrator.host}")
    private String orchestratorHost;
    @Autowired
    private OrchestratorRequestService orchestratorRequestService;
    @Autowired
    private KafkaProducerService kafkaProducerService;
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
    private FileService fileService;
    @Autowired
    private TelegramMessageCheckerService telegramMessageCheckerService;
    @Autowired
    private TelegramMessageService telegramMessageService;
    private final Integer MILLISECONDS_CONVERSION = 1000;
    private final ZoneOffset MOSCOW_OFFSET = ZoneOffset.of("+03:00");
    private final Boolean BOT = true;
    private final Integer EDIT_COUNT_MESSAGES = 1;

    @Autowired
    public OverMoneyBot(List<OverMoneyCommand> allCommands) {
        super();
        allCommands.forEach(this::register);
    }

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
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage()) {
            Message receivedMessage = update.getMessage();
            Long chatId = receivedMessage.getChatId();
            if (receivedMessage.getLeftChatMember() != null) {
                User remoteUser = receivedMessage.getLeftChatMember();
                if (!remoteUser.getIsBot()) {
                    String backupFileName = fileService.createBackupFileToRemoteInChatUser(chatId, remoteUser.getId());
                    sendBuckUpFile(remoteUser.getId().toString(), backupFileName);
                    orchestratorRequestService.removeChatMemberFromAccount(
                            chatMemberMapper.mapUserToChatMemberDTO(chatId, remoteUser));
                }
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
        if (update.hasMyChatMember()) {
            String status = update.getMyChatMember().getNewChatMember().getStatus();
            if (status.equals("left") && !update.hasMessage()) {
                Long chatId = update.getMyChatMember().getChat().getId();
                Long userId = update.getMyChatMember().getFrom().getId();
                String backUpFileName = fileService.createBackupFileToRemoteInChatUser(chatId, userId);
                sendBuckUpFile(userId.toString(), backUpFileName);
            }
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
            } else if (callbackQuery.getData()
                    .equals(InlineKeyboardCallback.MERGE_CATEGORIES_AND_TRANSACTIONS.getData())) {
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
            log.info("recognition result of user with id " + userId + " and chatId " + chatId + " is: " +
                    receivedMessageText);
        } else if (message.hasText()) {
            receivedMessageText = message.getText();
        }
        return receivedMessageText;
    }

    private void botAnswer(Message receivedMessage) {
        Long chatId = receivedMessage.getChatId();
        Long userId = receivedMessage.getFrom().getId();
        Integer messageId = receivedMessage.getMessageId();
        String receivedMessageText = null;
        try {
            receivedMessageText = getReceivedMessage(receivedMessage).toLowerCase();
        } catch (VoiceProcessingException e) {
            log.error("An error occurred while processing the voice message: " + e.getMessage(), e);
            String errorMessage = "При обработке голосового сообщения произошла: " + e.getMessage();
            sendMessage(chatId, errorMessage);
            return;
        }
        Message replyToMessage = receivedMessage.getReplyToMessage();
        LocalDateTime date = Instant.ofEpochMilli((long) receivedMessage.getDate() * MILLISECONDS_CONVERSION)
                .atOffset(MOSCOW_OFFSET).toLocalDateTime();
        TransactionMessageDTO transactionMessageDTO = TransactionMessageDTO.builder()
                .message(receivedMessageText)
                .userId(userId)
                .chatId(chatId)
                .date(date)
                .build();
        if (telegramMessageCheckerService.isNonTransactionalMessageMentionedToSomeone(receivedMessageText)
                || receivedMessageText.equals(BLANK_MESSAGE)) {
            return;
        }
        if (replyToMessage != null) {
            List<TelegramMessage> messages = telegramMessageService.
                    getTelegramMessageMessageIdAndChatId(replyToMessage.getMessageId(), chatId);
            if (messages == null) {
                if (!userId.equals(replyToMessage.getFrom().getId())) {
                    sendMessage(chatId, INVALID_UPDATE_TRANSACTION_TEXT);
                    return;
                }
                processTransaction(chatId, messageId, transactionMessageDTO);
                return;
            }
            if (!receivedMessageText.equals(COMMAND_TO_DELETE_TRANSACTION) &&
                    !receivedMessageText.equalsIgnoreCase(replyToMessage.getText())) {
                if (messages.size() > EDIT_COUNT_MESSAGES) {
                    sendMessage(chatId, MULTILINE_MESSAGE_CANNOT_BE_EDITED);
                    return;
                }
                messages.forEach(message -> {
                    UUID idTransaction = message.getIdTransaction();
                    TransactionDTO previousTransaction = orchestratorRequestService.getTransactionById(idTransaction);
                    transactionMessageDTO.setDate(previousTransaction.getDate());
                    updateTransaction(transactionMessageDTO, idTransaction, chatId, messageId);
                });
                return;
            }
        }
        switch (receivedMessageText) {
            case COMMAND_TO_DELETE_TRANSACTION:
                if (replyToMessage != null) {
                    deleteTransaction(replyToMessage, chatId);
                    break;
                }
            default:
                processTransaction(chatId, messageId, transactionMessageDTO);
                break;
        }
    }

    public StatusMailing sendMessage(Long chatId, String messageText) {
        SendMessage message = new SendMessage(chatId.toString(), messageText);
        try {
            execute(message);
            return StatusMailing.SUCCESS;
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
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

    public void sendBuckUpFile(String userChatId, String fileName) {
        File file = new File(fileName);
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(userChatId);
        sendDocument.setDocument(new InputFile(file));
        try {
            execute(sendDocument);
            file.delete();
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void deleteTransaction(Message replyToMessage, Long chatId) {
        try {
            telegramMessageService.deleteTransactionsById(replyToMessage.getMessageId(), chatId);
            sendMessage(chatId, SUCCESSFUL_DELETION_TRANSACTION);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sendMessage(chatId, INVALID_TRANSACTION_TO_DELETE);
        }
    }

    private void updateTransaction(TransactionMessageDTO transactionMessageDTO, UUID idTransaction, Long chatId,
                                   Integer messageId) {
        try {
            TransactionResponseDTO transactionResponseDTO = orchestratorRequestService
                    .submitTransactionForPatch(transactionMessageDTO, idTransaction);
            telegramMessageService.saveTelegramMessage(TelegramMessage.builder()
                    .messageId(messageId)
                    .chatId(chatId)
                    .idTransaction(transactionResponseDTO.getId())
                    .build());
            sendMessage(chatId,
                    SUCCESSFUL_UPDATE_TRANSACTION_TEXT +
                            transactionMapper.mapTransactionResponseToTelegramMessage(transactionResponseDTO));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sendMessage(chatId, TRANSACTION_MESSAGE_INVALID);
        }
    }

    private void processTransaction(Long chatId, Integer messageId, TransactionMessageDTO transactionMessageDTO) {

        if (switcher.equals("orchestrator")) {
            try {
                TransactionResponseDTO transactionResponseDTO = orchestratorRequestService
                        .sendTransaction(transactionMessageDTO);
                telegramMessageService.saveTelegramMessage(TelegramMessage.builder()
                        .messageId(messageId)
                        .chatId(chatId)
                        .idTransaction(transactionResponseDTO.getId()).build());
                sendMessage(chatId, transactionMapper
                        .mapTransactionResponseToTelegramMessage(transactionResponseDTO));
            } catch (FeignException.InternalServerError e) {
                log.error(e.getMessage(), e);
                sendMessage(chatId, MESSAGE_NOT_REGISTERED);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                sendMessage(chatId, TRANSACTION_MESSAGE_INVALID); // todo возможно тут 1
            }
        } else if (switcher.equals("kafka")) {
            List<TransactionMessageDTO> messageDTOList = splitToTransactionDtoList(transactionMessageDTO);
            for (TransactionMessageDTO messageDTO : messageDTOList) {
                CompletableFuture<TransactionResponseDTO> future =
                        kafkaProducerService.sendTransaction(messageDTO);

                future.thenAccept(transactionResponseDTO -> {
                    if (transactionResponseDTO.getComment().equals("error")) {
                        throw new RuntimeException("Невалидное сообщение");
                    }
                    telegramMessageService.saveTelegramMessage(TelegramMessage.builder()
                            .messageId(messageId)
                            .chatId(chatId)
                            .idTransaction(transactionResponseDTO.getId()).build());
                    sendMessage(chatId, transactionMapper
                            .mapTransactionResponseToTelegramMessage(transactionResponseDTO));
                }).exceptionally(e -> {
                    if (e.getCause() instanceof RuntimeException &&
                            "Невалидное сообщение".equals(e.getCause().getMessage())) {
                        log.error(e.getMessage(), e);
                        sendMessage(chatId, TRANSACTION_MESSAGE_INVALID);
                    } else {
                        log.error(e.getMessage(), e);
                        sendMessage(chatId, NETWORK_ERROR);
                    }
                    return null;
                });
            }
        }
    }

    private List<TransactionMessageDTO> splitToTransactionDtoList(TransactionMessageDTO transactionMessageDTO) {
        return Arrays.stream(transactionMessageDTO.getMessage().split("\n"))
                .map(message -> {
                    TransactionMessageDTO dto = new TransactionMessageDTO();
                    dto.setUserId(transactionMessageDTO.getUserId());
                    dto.setChatId(transactionMessageDTO.getChatId());
                    dto.setMessage(message);
                    dto.setDate(transactionMessageDTO.getDate());
                    dto.setBindingUuid(UUID.randomUUID());
                    return dto;
                }).collect(Collectors.toList());
    }
}