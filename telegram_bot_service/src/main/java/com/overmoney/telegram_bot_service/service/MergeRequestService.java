package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.mapper.MergeRequestMapper;
import com.overmoney.telegram_bot_service.model.MergeRequest;
import com.overmoney.telegram_bot_service.repository.MergeRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class MergeRequestService {
    @Autowired
    private MergeRequestRepository mergeRequestRepository;
    @Autowired
    private MergeRequestMapper mergeRequestMapper;

    public void saveMergeRequestMessage(Message message) {
        saveMergeRequest(mergeRequestMapper.mapMergeRequestMessageToMergeRequest(message));
    }

    public void saveMergeRequest(MergeRequest mergeRequest) {
        mergeRequestRepository.save(mergeRequest);
    }

    public MergeRequest getMergeRequestByChatId(Long chatId) {
        return mergeRequestRepository.getMergeRequestByChatId(chatId);
    }

    @Transactional
    public void updateMergeRequestCompletionByChatId(Long chatId) {
        mergeRequestRepository.updateMergeRequestCompletionByChatId(chatId);
    }
}