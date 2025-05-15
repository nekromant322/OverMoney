package com.override.payment_service.kafka.consumerproducer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(name = "service.transaction.processing", havingValue = "kafka")
@KafkaListener(topics = "${spring.kafka.topics.request}")
public class TransactionListener {

    private String processing;

    public String getProcessing() {
        return processing;
    }

    public void setProcessing(String processing) {
        this.processing = processing;
    }
}
