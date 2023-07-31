package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.model.MessageTelegram;
import com.overmoney.telegram_bot_service.repository.MessageTelegramRepository;
import com.overmoney.telegram_bot_service.utils.TestFieldsUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageTelegramServiceTest {
    @InjectMocks
    private MessageTelegramService messageTelegramService;
    @Mock
    private MessageTelegramRepository messageTelegramRepository;
    @Mock
    private OrchestratorRequestService orchestratorRequestService;


    @Test
    public void saveMessageTelegramTest() {
        MessageTelegram messageTelegram = TestFieldsUtil.generateMessageTelegram();
        messageTelegramService.saveMessageTelegram(messageTelegram);
        verify(messageTelegramRepository, times(1)).save(messageTelegram);
    }

    @Test
    public void deleteMessageTelegramByIdTransactionTest() {
        MessageTelegram messageTelegram = TestFieldsUtil.generateMessageTelegram();
        messageTelegramService.deleteMessageTelegramByIdTransaction(messageTelegram.getIdTransaction());
        verify(messageTelegramRepository, times(1)).deleteByIdTransaction(messageTelegram.getIdTransaction());
    }

    @Test
    public void deleteTransactionByIdTest() {
        MessageTelegram messageTelegram = TestFieldsUtil.generateMessageTelegram();
        when(messageTelegramRepository.findById(any())).thenReturn(Optional.of(messageTelegram));

        messageTelegramService.deleteTransactionById(messageTelegram.getId());
        verify(orchestratorRequestService, times(1)).deleteTransactionById(messageTelegram.getIdTransaction());
    }

}
