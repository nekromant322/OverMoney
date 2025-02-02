package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.model.TelegramMessage;
import com.overmoney.telegram_bot_service.repository.TelegramMessageRepository;
import com.overmoney.telegram_bot_service.utils.TestFieldsUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TelegramMessageServiceTest {
    @InjectMocks
    private TelegramMessageService telegramMessageService;
    @Mock
    private TelegramMessageRepository telegramMessageRepository;
    @Mock
    private OrchestratorRequestService orchestratorRequestService;


    @Test
    public void saveTelegramMessageTest() {
        TelegramMessage telegramMessage = TestFieldsUtil.generateTelegramMessage();
        telegramMessageService.saveTelegramMessage(telegramMessage);
        verify(telegramMessageRepository, times(1)).save(telegramMessage);
    }

    @Test
    public void deleteTelegramMessageByIdTransactionTest() {
        TelegramMessage telegramMessage = TestFieldsUtil.generateTelegramMessage();
        telegramMessageService.deleteTelegramMessageByIdTransactions(Collections.singletonList(telegramMessage.getIdTransaction()));
        verify(telegramMessageRepository, times(1))
                .deleteByIdTransactions(Collections.singletonList(telegramMessage.getIdTransaction()));
    }

    @Test
    public void deleteTransactionByIdsTest() {
        TelegramMessage telegramMessage = TestFieldsUtil.generateTelegramMessage();
        when(telegramMessageRepository.findTgMessageIdsByMessageIdAndChatId(any(), any())).thenReturn(Collections.singletonList(telegramMessage));

        telegramMessageService.deleteTransactionById(telegramMessage.getMessageId(), telegramMessage.getChatId());
        verify(orchestratorRequestService, times(1))
                .deleteTransactionByIds(Collections.singletonList(telegramMessage.getIdTransaction()));
    }

}
