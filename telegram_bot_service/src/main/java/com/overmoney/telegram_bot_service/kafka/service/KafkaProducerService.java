package com.overmoney.telegram_bot_service.kafka.service;

import com.override.dto.TransactionMessageDTO;
import com.override.dto.TransactionResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.concurrent.*;

@Service
@Slf4j
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, TransactionMessageDTO> kafkaTemplate;

    private final Map<Long, CompletableFuture<TransactionResponseDTO>> responseFutures = new ConcurrentHashMap<>();

    @Transactional
    public CompletableFuture<TransactionResponseDTO> sendTransaction(
            Long chatId, TransactionMessageDTO transactionMessageDTO) {
        CompletableFuture<TransactionResponseDTO> future = new CompletableFuture<>();
        responseFutures.put(chatId, future);

        ListenableFuture<SendResult<String, TransactionMessageDTO>> kafkaResultFuture =
                kafkaTemplate.send("transaction-request-events-topic", transactionMessageDTO);

        kafkaResultFuture.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, TransactionMessageDTO> result) {
                log.info(result.getRecordMetadata().topic());
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error(ex.getMessage());
                future.completeExceptionally(ex);
            }
        });

        return future;
    }

    public void completeResponse(Long chatId, TransactionResponseDTO responseDTO) {
        CompletableFuture<TransactionResponseDTO> future = responseFutures.remove(chatId);
        if (future != null) {
            future.complete(responseDTO);
        }
    }
}
