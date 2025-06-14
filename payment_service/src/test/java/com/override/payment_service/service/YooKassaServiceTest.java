package com.override.payment_service.service;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.dto.YooKassaRequestDTO;
import com.override.dto.YooKassaResponseDTO;
import com.override.dto.constants.Currency;
import com.override.dto.constants.PaymentStatus;
import com.override.payment_service.client.YooKassaClient;
import com.override.payment_service.mapper.YooKassaMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;

public class YooKassaServiceTest {

    private static final String TEST_ORDER_ID = "order123";
    private static final String TEST_IDEMPOTENCE_KEY = "test-key";
    private static final String TEST_CONFIRM_URL = "https://payment.com/confirm";
    private static final String TEST_RETURN_URL = "https://example.com/success";
    private static final BigDecimal TEST_AMOUNT = BigDecimal.valueOf(100);

    @Test
    public void testCreatePayment_success() {
        YooKassaClient mockClient = mock(YooKassaClient.class);
        YooKassaMapper mockMapper = mock(YooKassaMapper.class);
        YooKassaService service = new YooKassaService(mockClient, mockMapper);

        PaymentRequestDTO request = PaymentRequestDTO.builder()
                .orderId(TEST_ORDER_ID)
                .amount(TEST_AMOUNT)
                .currency(Currency.RUB)
                .description("Test payment")
                .returnUrl(TEST_RETURN_URL)
                .build();

        YooKassaRequestDTO mappedRequest = YooKassaRequestDTO.builder().build();
        when(mockMapper.mapToYooKassaRequest(request)).thenReturn(mappedRequest);
        when(mockMapper.generateIdempotenceKey()).thenReturn(TEST_IDEMPOTENCE_KEY);

        YooKassaResponseDTO.Confirmation confirmation = YooKassaResponseDTO.Confirmation.builder()
                .confirmationUrl(TEST_CONFIRM_URL)
                .build();

        YooKassaResponseDTO yooKassaResponse = YooKassaResponseDTO.builder()
                .status(PaymentStatus.PENDING)
                .confirmation(confirmation)
                .build();

        when(mockClient.createPayment(eq(TEST_IDEMPOTENCE_KEY), eq(mappedRequest)))
                .thenReturn(yooKassaResponse);

        PaymentResponseDTO expectedResponse = PaymentResponseDTO.builder()
                .orderId(TEST_ORDER_ID)
                .paymentUrl(TEST_CONFIRM_URL)
                .status(PaymentStatus.PENDING)
                .build();

        when(mockMapper.mapToPaymentResponse(yooKassaResponse, request))
                .thenReturn(expectedResponse);

        PaymentResponseDTO result = service.createPayment(request);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(mockMapper).mapToYooKassaRequest(request);
        verify(mockMapper).generateIdempotenceKey();
        verify(mockClient).createPayment(TEST_IDEMPOTENCE_KEY, mappedRequest);
        verify(mockMapper).mapToPaymentResponse(yooKassaResponse, request);
    }
}