package com.override.payment_service.kafka.consumerproducer;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.dto.constants.Currency;
import com.override.dto.constants.PaymentStatus;
import com.override.payment_service.service.YooKassaService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;

public class KafkaConsumerServiceTest {

    @Test
    public void testListenForPaymentRequests_success() {
        YooKassaService yooKassaService = mock(YooKassaService.class);
        KafkaProducerService kafkaProducerService = mock(KafkaProducerService.class);

        KafkaConsumerService consumerService = new KafkaConsumerService(yooKassaService, kafkaProducerService);

        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setOrderId("order456");
        request.setAmount(BigDecimal.valueOf(200));
        request.setCurrency(Currency.RUB);
        request.setDescription("test");
        request.setReturnUrl("url");

        PaymentResponseDTO responseDTO = new PaymentResponseDTO();
        responseDTO.setOrderId("order456");
        responseDTO.setPaymentUrl("payment-url");
        responseDTO.setStatus(PaymentStatus.SUCCESS);

        when(yooKassaService.createPayment(any())).thenReturn(responseDTO);

        consumerService.listenForPaymentRequests(request, "key123", "correlationId123");

        verify(kafkaProducerService, times(1)).sendPaymentResponse(eq("key123"),
                eq("correlationId123"), any());
    }
}