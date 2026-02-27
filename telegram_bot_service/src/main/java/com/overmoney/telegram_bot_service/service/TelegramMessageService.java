package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.model.TelegramMessage;
import com.overmoney.telegram_bot_service.repository.TelegramMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TelegramMessageService {

    @Autowired
    private TelegramMessageRepository telegramMessageRepository;
    @Autowired
    private OrchestratorRequestService orchestratorRequestService;

    public void saveTelegramMessage(TelegramMessage telegramMessage) {
        telegramMessageRepository.save(telegramMessage);
    }

    @Transactional
    public void deleteTelegramMessageByIdTransaction(UUID id) {
        telegramMessageRepository.deleteByIdTransaction(id);
    }

    @Transactional
    public void deleteTelegramMessageByIdTransactions(List<UUID> ids) {
        telegramMessageRepository.deleteByIdTransactions(ids);
    }

    public void deleteTransactionsById(Integer messageId, Long chatId) {
        List<UUID> telegramMessageIds = telegramMessageRepository
                .findTgMessageIdsByMessageIdAndChatId(messageId, chatId).stream().map(TelegramMessage::getIdTransaction)
                .collect(Collectors.toList());
        orchestratorRequestService.deleteTransactionByIds(telegramMessageIds);
    }

    public List<TelegramMessage> getTelegramMessageMessageIdAndChatId(Integer messageId, Long chatId) {
        return telegramMessageRepository.findByMessageIdAndChatId(messageId, chatId);
    }

    public Set<Long> getAllUniqChatIds() {
        return new HashSet<>(telegramMessageRepository.findUniqChatIds());
    }
}
