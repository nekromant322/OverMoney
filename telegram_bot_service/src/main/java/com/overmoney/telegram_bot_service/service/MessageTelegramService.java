package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.model.MessageTelegram;
import com.overmoney.telegram_bot_service.repository.MessageTelegramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MessageTelegramService {

    @Autowired
    private MessageTelegramRepository messageTelegramRepository;
    @Autowired
    private OrchestratorRequestService orchestratorRequestService;

    public void saveMessageTelegram(MessageTelegram messageTelegram) {
        messageTelegramRepository.save(messageTelegram);
    }

    @Transactional
    public void deleteMessageTelegramByIdTransaction(UUID id) {
        messageTelegramRepository.deleteByIdTransaction(id);
    }

    public void deleteTransactionById(Integer idMessage) {
        orchestratorRequestService.deleteTransaction(messageTelegramRepository.findById(idMessage).get().getIdTransaction());
    }
}
