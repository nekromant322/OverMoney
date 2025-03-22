package com.override.orchestrator_service.kafka.consumerproducer;

import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.service.TransactionProcessingService;
import com.override.orchestrator_service.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import javax.management.InstanceNotFoundException;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.override.orchestrator_service.utils.TestFieldsUtil.generateTestAccount;
import static com.override.orchestrator_service.utils.TestFieldsUtil.generateTestCategory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionListenerTest {

    @Mock
    private TransactionProcessingService transactionProcessingService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private KafkaTemplate<String, TransactionResponseDTO> kafkaTemplate;

    @InjectMocks
    private TransactionListener transactionTestReceiver;

    @Value("${spring.kafka.topics.response}")
    private String responseTopic;

    @Test
    public void testProcessTestTransaction() throws InstanceNotFoundException {

        UUID id = UUID.randomUUID();

        TransactionMessageDTO transactionMessageDTO = TransactionMessageDTO.builder()
                .message("пиво 100")
                .userId(123L)
                .chatId(404723191L)
                .build();

        Transaction transaction = Transaction.builder()
                .id(id)
                .message("пиво")
                .amount(100d)
                .date(LocalDateTime.now())
                .category(generateTestCategory())
                .account(generateTestAccount())
                .build();

        TransactionResponseDTO responseDTO = TransactionResponseDTO.builder()
                .id(id)
                .type("Расходы")
                .category("Нераспознанное")
                .amount("100")
                .comment("пиво")
                .chatId(404723191L)
                .build();


        when(transactionProcessingService.processTransaction(transactionMessageDTO)).thenReturn(transaction);
        doNothing().when(transactionService).saveTransaction(transaction);
        doNothing().when(transactionProcessingService)
                .suggestCategoryToProcessedTransaction(transaction);


        when(transactionMapper.mapTransactionToTelegramResponse(transaction, null)).thenReturn(responseDTO);

        SettableListenableFuture<SendResult<String, TransactionResponseDTO>> future = new SettableListenableFuture<>();
        future.set(new SendResult<>(null, null));
        when(kafkaTemplate.send(eq(responseTopic),
                any(TransactionResponseDTO.class))).thenReturn(future);

        transactionTestReceiver.processTransaction(transactionMessageDTO);

        ArgumentCaptor<TransactionResponseDTO> responseCaptor = ArgumentCaptor.forClass(TransactionResponseDTO.class);
        verify(kafkaTemplate).send(eq(responseTopic), responseCaptor.capture());
        assertEquals(responseDTO, responseCaptor.getValue());
    }
    @Test
    public void testProcessTransactionError() {
        TransactionMessageDTO transactionMessageDTO = new TransactionMessageDTO();
        transactionMessageDTO.setChatId(123L);

        when(transactionProcessingService.processTransaction(transactionMessageDTO)).thenThrow(new RuntimeException());

        SettableListenableFuture<SendResult<String, TransactionResponseDTO>> future = new SettableListenableFuture<>();
        future.set(new SendResult<>(null, null));
        when(kafkaTemplate.send(eq(responseTopic),
                any(TransactionResponseDTO.class))).thenReturn(future);

        transactionTestReceiver.processTransaction(transactionMessageDTO);

        ArgumentCaptor<TransactionResponseDTO> responseCaptor = ArgumentCaptor.forClass(TransactionResponseDTO.class);
        verify(kafkaTemplate).send(eq(responseTopic), responseCaptor.capture());
        assertTrue(responseCaptor.getValue().getComment().startsWith("Ошибка в обработке транзакции:"));
        assertEquals(transactionMessageDTO.getChatId(), responseCaptor.getValue().getChatId());
    }
}