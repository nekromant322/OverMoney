package com.override.payment_service.kafka.consumerproducer;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.payment_service.service.YooKassaService;
import com.override.payment_service.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaConsumerServiceTest {

    @Mock
    private YooKassaService yooKassaService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @Test
    public void testListenForPaymentRequests_Success() {
        PaymentRequestDTO request = TestUtil.createTestPaymentRequest();
        PaymentResponseDTO mockResponse = new PaymentResponseDTO();
        mockResponse.setPaymentUrl("https://yookassa.test/confirm");
        mockResponse.setOrderId(request.getOrderId());
        mockResponse.setStatus("success");

        when(yooKassaService.createPayment(request)).thenReturn(mockResponse);

        kafkaConsumerService.listenForPaymentRequests(
                request,
                "test-key",
                "test-correlation-id"
        );

        verify(kafkaProducerService, times(1))
                .sendPaymentResponse(eq("test-key"), eq("test-correlation-id"), any(PaymentResponseDTO.class));
    }

    @Test
    public void testListenForPaymentRequests_Failure() {
        PaymentRequestDTO request = TestUtil.createTestPaymentRequest();

        when(yooKassaService.createPayment(request)).thenThrow(new RuntimeException("Payment failed"));

        kafkaConsumerService.listenForPaymentRequests(
                request,
                "test-key",
                "test-correlation-id"
        );

        verify(kafkaProducerService, times(1))
                .sendPaymentResponse(eq("test-key"), eq("test-correlation-id"), argThat(response ->
                        response.getStatus().equals("failed") &&
                                response.getPaymentUrl() == null
                ));
    }
}