package com.override.payment_service.kafka.service;

import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, TransactionMessageDTO> kafkaTemplate;

    private final Map<UUID, CompletableFuture<TransactionResponseDTO>> responseFutures = new ConcurrentHashMap<>();

    @Value("${spring.kafka.topics.request}")
    private String requestTopic;

    public CompletableFuture<TransactionResponseDTO> sendTransaction(TransactionMessageDTO transactionMessageDTO) {
        CompletableFuture<TransactionResponseDTO> future = new CompletableFuture<>();
        responseFutures.put(transactionMessageDTO.getBindingUuid(), future);

        ListenableFuture<SendResult<String, TransactionMessageDTO>> kafkaResultFuture =
                kafkaTemplate.send(requestTopic, transactionMessageDTO);

        kafkaResultFuture.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, TransactionMessageDTO> result) {
                log.info("Транзакция " + result.getProducerRecord().value().toString()
                        + " была успешно обработана");
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error(ex.getMessage(), ex);
                future.completeExceptionally(ex);
            }
        });

        return future;
    }

    public void completeResponse(TransactionResponseDTO responseDTO) {
        CompletableFuture<TransactionResponseDTO> future = responseFutures.remove(responseDTO.getBindingUuid());
        if (future != null) {
            future.complete(responseDTO);
        }
    }
}
