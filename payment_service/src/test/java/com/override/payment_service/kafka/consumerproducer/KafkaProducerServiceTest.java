package com.override.payment_service.kafka.consumerproducer;

import com.override.dto.PaymentResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, PaymentResponseDTO> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @Test
    public void testSendPaymentResponse() {
        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setOrderId("test-order-123");
        response.setPaymentUrl("https://yookassa.test/confirm");
        response.setStatus("success");

        kafkaProducerService.sendPaymentResponse("test-key", "test-correlation-id", response);

        verify(kafkaTemplate).send(any(Message.class));
    }
}