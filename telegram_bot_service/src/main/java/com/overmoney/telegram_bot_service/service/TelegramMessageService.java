package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.model.TelegramMessage;
import com.overmoney.telegram_bot_service.repository.TelegramMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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

    public void deleteTransactionById(Integer messageId) {
        TelegramMessage telegramMessage = telegramMessageRepository.findById(messageId).get();
        orchestratorRequestService.deleteTransactionById(telegramMessage.getIdTransaction());
    }
}