package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.model.MergeRequest;
import com.overmoney.telegram_bot_service.repository.MergeRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MergeRequestServiceTest {
    @InjectMocks
    private MergeRequestService mergeRequestService;
    @Mock
    private MergeRequestRepository mergeRequestRepository;

    @Test
    public void saveMergeRequestTest() {
        MergeRequest mergeRequest = new MergeRequest();

        mergeRequestService.saveMergeRequest(mergeRequest);

        verify(mergeRequestRepository, times(1)).save(mergeRequest);
    }

    @Test
    public void getMergeRequestByChatIdTest() {
        Long chatId = -123L;

        mergeRequestService.getMergeRequestByChatId(chatId);

        verify(mergeRequestRepository, times(1)).getMergeRequestByChatId(chatId);
    }

    @Test
    public void updateMergeRequestCompletionByChatIdTest() {
        Long chatId = -123L;

        mergeRequestService.updateMergeRequestCompletionByChatId(chatId);

        verify(mergeRequestRepository, times(1)).updateMergeRequestCompletionByChatId(chatId);
    }
}
