package com.override.orchestrator_service.kafka;

import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import com.override.orchestrator_service.kafka.consumerproducer.TransactionReceiver;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.service.TransactionProcessingService;
import com.override.orchestrator_service.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import javax.management.InstanceNotFoundException;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionReceiverTest {

    @Mock
    private TransactionProcessingService transactionProcessingService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private KafkaTemplate<String, TransactionResponseDTO> kafkaTemplate;

    @InjectMocks
    private TransactionReceiver transactionTestReceiver;

    @Test
    public void testProcessTestTransaction() throws InstanceNotFoundException, ExecutionException, InterruptedException {

        TransactionMessageDTO transactionMessageDTO = new TransactionMessageDTO();
        Transaction transaction = new Transaction();
        TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO();

        when(transactionProcessingService.processTransaction(transactionMessageDTO)).thenReturn(transaction);
        when(transactionMapper.mapTransactionToTelegramResponse(transaction)).thenReturn(transactionResponseDTO);

        SettableListenableFuture<SendResult<String, TransactionResponseDTO>> future = new SettableListenableFuture<>();
        future.set(mock(SendResult.class));
        when(kafkaTemplate.send(anyString(), any(TransactionResponseDTO.class))).thenReturn(future);

        transactionTestReceiver.processTransaction(transactionMessageDTO);

        verify(transactionProcessingService).processTransaction(transactionMessageDTO);
        verify(transactionService).saveTransaction(transaction);
        verify(transactionProcessingService).suggestCategoryToProcessedTransaction(transactionMessageDTO, transaction.getId());
        verify(kafkaTemplate).send("transaction-response-events-topic", transactionResponseDTO);

    }
}