package com.override.payment_service.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.override.dto.PaymentResponseDTO;
import com.override.dto.constants.PaymentStatus;
import com.override.payment_service.service.SubscriptionService;
import com.override.payment_service.service.YooKassaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    private static final String TEST_ORDER_ID = "order-123";
    private static final String TEST_PAYMENT_ID = "payment-456";
    private static final Long TEST_CHAT_ID = 12345L;
    private static final String TEST_RETURN_URL = "https://return.url";
    private static final String TEST_PAYMENT_URL = "https://payment.url";
    private static final BigDecimal TEST_AMOUNT = BigDecimal.valueOf(100);
    private static final LocalDateTime TEST_DATE = LocalDateTime.now();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private YooKassaService yooKassaService;

    @MockBean
    private SubscriptionService subscriptionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void checkPaymentStatus_ShouldUpdateSubscription() throws Exception {
        PaymentResponseDTO response = PaymentResponseDTO.builder()
                .paymentId(TEST_PAYMENT_ID)
                .status(PaymentStatus.SUCCESS)
                .build();

        when(yooKassaService.checkPaymentStatus(anyString()))
                .thenReturn(response);

        mockMvc.perform(get("/payments/{paymentId}/status", TEST_PAYMENT_ID))
                .andExpect(status().isOk());

        verify(subscriptionService).updateSubscriptionStatus(TEST_PAYMENT_ID, PaymentStatus.SUCCESS);
    }
}