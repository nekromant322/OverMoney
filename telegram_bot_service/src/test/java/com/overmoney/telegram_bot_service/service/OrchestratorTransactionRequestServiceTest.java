package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.feign.OrchestratorFeign;
import com.override.dto.TransactionMessageDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.overmoney.telegram_bot_service.utils.TestFieldsUtil.generateTransactionDTO;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrchestratorTransactionRequestServiceTest {

    @InjectMocks
    private OrchestratorTransactionRequestService orchestratorRequestService;

    @Mock
    private OrchestratorFeign orchestratorFeign;

    @Test
    public void sendTransactionTest() {
        TransactionMessageDTO transaction = generateTransactionDTO();

        orchestratorRequestService.sendTransaction(transaction);

        verify(orchestratorFeign, times(1)).sendTransaction(transaction);
    }
}
