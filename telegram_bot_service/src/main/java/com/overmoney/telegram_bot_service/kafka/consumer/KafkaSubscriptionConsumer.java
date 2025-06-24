package com.overmoney.telegram_bot_service.kafka.consumer;

import com.overmoney.telegram_bot_service.constants.KafkaConstants;
import com.overmoney.telegram_bot_service.service.PaymentResponseHandler;
import com.override.dto.PaymentResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "service.transaction.processing", havingValue = "kafka")
@Slf4j
public class KafkaSubscriptionConsumer {

    private final PaymentResponseHandler paymentResponseHandler;

    public KafkaSubscriptionConsumer(PaymentResponseHandler paymentResponseHandler) {
        this.paymentResponseHandler = paymentResponseHandler;
    }

    @KafkaListener(
            topics = KafkaConstants.PAYMENT_RESPONSES_TOPIC,
            groupId = KafkaConstants.TELEGRAM_BOT_GROUP
    )
    public void listenForPaymentResponses(
            @Payload PaymentResponseDTO paymentResponse,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key) {

        log.info("Получен ответ об оплате подписки: {}", paymentResponse.getOrderId());
        paymentResponseHandler.handlePaymentResponse(paymentResponse);
    }
}