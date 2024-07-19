package com.override.orchestrator_service.kafka.consumerproducer;

import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.service.TransactionProcessingService;
import com.override.orchestrator_service.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Slf4j
@KafkaListener(topics = "transaction-request-events-topic")
public class TransactionListener {
    @Autowired
    private TransactionProcessingService transactionProcessingService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private KafkaTemplate<String, TransactionResponseDTO> kafkaTemplate;

    @KafkaHandler
    @Transactional
    public void processTransaction(TransactionMessageDTO transaction) {

        try {
            Transaction transactionResult = transactionProcessingService.processTransaction(transaction);
            transactionService.saveTransaction(transactionResult);
            transactionProcessingService.suggestCategoryToProcessedTransaction(transaction, transactionResult.getId());

            kafkaTemplate.send("transaction-response-events-topic", transactionMapper
                    .mapTransactionToTelegramResponse(transactionResult));
        } catch (Exception e) {
            TransactionResponseDTO errorResponse = new TransactionResponseDTO();
            errorResponse.setComment("error");
            errorResponse.setChatId(transaction.getChatId());
            kafkaTemplate.send("transaction-response-events-topic", errorResponse);
        }
    }
}
