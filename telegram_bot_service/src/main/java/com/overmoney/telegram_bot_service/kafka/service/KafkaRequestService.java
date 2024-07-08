package com.overmoney.telegram_bot_service.kafka.service;

import com.overmoney.telegram_bot_service.kafka.event.KafkaConsumerTransactionResponse;
import com.overmoney.telegram_bot_service.service.RequestService;
import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
@ConditionalOnProperty(name = "service", havingValue = "kafka", matchIfMissing = true)
public class KafkaRequestService implements RequestService{

    @Autowired
    private KafkaTemplate<String, TransactionMessageDTO> kafkaTemplate;

    @Autowired
    private KafkaConsumerTransactionResponse transactionResponse;

    @Override
    public TransactionResponseDTO sendTransaction(TransactionMessageDTO transaction)
            throws ExecutionException, InterruptedException, TimeoutException {

        kafkaTemplate.send("transaction-request-events-topic", transaction).get();

        CompletableFuture<TransactionResponseDTO> future = transactionResponse.getFuture();
        log.info("Получена обработанная транзакция с ID: {}", future.get().getId());

        return future.get(10, TimeUnit.SECONDS);
    }
}
