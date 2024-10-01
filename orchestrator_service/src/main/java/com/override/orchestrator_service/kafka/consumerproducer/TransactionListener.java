package com.override.orchestrator_service.kafka.consumerproducer;

import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import com.override.orchestrator_service.mapper.TransactionMapper;
import com.override.orchestrator_service.model.Transaction;
import com.override.orchestrator_service.service.TransactionProcessingService;
import com.override.orchestrator_service.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Slf4j
@ConditionalOnProperty(name = "service.transaction.processing", havingValue = "kafka")
@KafkaListener(topics = "${spring.kafka.topics.request}")
public class TransactionListener {
    @Autowired
    private TransactionProcessingService transactionProcessingService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private KafkaTemplate<String, TransactionResponseDTO> kafkaTemplate;

    @Value("${spring.kafka.topics.response}")
    private String responseTopic;

    @KafkaHandler
    @Transactional
    public void processTransaction(TransactionMessageDTO transaction) {

        try {
            Transaction currentTransaction = transactionProcessingService.processTransaction(transaction);
            transactionService.saveTransaction(currentTransaction);
            transactionProcessingService.suggestCategoryToProcessedTransaction(currentTransaction);

            kafkaTemplate.send(responseTopic, transactionMapper
                    .mapTransactionToTelegramResponse(currentTransaction));
        } catch (Exception e) {
            TransactionResponseDTO errorResponse = new TransactionResponseDTO();
            errorResponse.setComment("error");
            errorResponse.setChatId(transaction.getChatId());
            log.error(e.getMessage());
            kafkaTemplate.send(responseTopic, errorResponse);
        }
    }
}
