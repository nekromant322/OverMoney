package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.feign.OrchestratorFeign;
import com.overmoney.telegram_bot_service.model.TransactionDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.overmoney.telegram_bot_service.utils.TestFieldsUtil.generateTransactionDTO;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrchestratorRequestServiceTest {

    @InjectMocks
    private OrchestratorRequestService orchestratorRequestService;

    @Mock
    private OrchestratorFeign orchestratorFeign;

    @Test
    public void sendTransactionTest() {
        TransactionDTO transaction = generateTransactionDTO();

        orchestratorRequestService.sendTransaction(transaction);

        verify(orchestratorFeign, times(1)).sendTransaction(transaction);
    }

    @Test
    public void sendVoiceMessageTest() {
        byte[] voiceMessageByteArray = {1, 2, 3};

        orchestratorRequestService.sendVoiceMessage(voiceMessageByteArray);

        verify(orchestratorFeign, times(1)).sendVoiceMessage(voiceMessageByteArray);
    }

}
