package com.override.orchestrator_service.kafka.consumerproducer;

import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.service.TransactionProcessingService;
import com.override.orchestrator_service.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.management.InstanceNotFoundException;
import javax.transaction.Transactional;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
@KafkaListener(topics = "transaction-request-events-topic")
@ConditionalOnProperty(name = "service.transaction.processing", havingValue = "kafka", matchIfMissing = true)
public class TransactionReceiver {
    @Autowired
    private TransactionProcessingService transactionProcessingService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    TransactionMapper transactionMapper;

    @Autowired
    private KafkaTemplate<String, TransactionResponseDTO> kafkaTemplate;

    @KafkaHandler
    @Transactional
    public void processTransaction(TransactionMessageDTO transaction)
            throws InstanceNotFoundException, ExecutionException, InterruptedException {
        log.info("Получена транзакция с сообщением: {}", transaction.toString());

        Transaction transactionResult = transactionProcessingService.processTransaction(transaction);
        transactionService.saveTransaction(transactionResult);
        transactionProcessingService.suggestCategoryToProcessedTransaction(transaction, transactionResult.getId());

        kafkaTemplate.send("transaction-response-events-topic", transactionMapper
                .mapTransactionToTelegramResponse(transactionResult)).get();
    }
}
