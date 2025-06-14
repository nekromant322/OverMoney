package com.override.payment_service.controller.rest;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.dto.constants.Currency;
import com.override.dto.constants.PaymentStatus;
import com.override.payment_service.service.YooKassaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class PaymentControllerTest {

    private static final String TEST_ORDER_ID = "abc";
    private static final String TEST_RETURN_URL = "https://return.url";
    private static final String TEST_CONFIRM_URL = "https://confirm.url";
    private static final BigDecimal TEST_AMOUNT = BigDecimal.valueOf(123);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private YooKassaService yooKassaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreatePayment() throws Exception {
        PaymentRequestDTO request = PaymentRequestDTO.builder()
                .orderId(TEST_ORDER_ID)
                .amount(TEST_AMOUNT)
                .currency(Currency.RUB)
                .returnUrl(TEST_RETURN_URL)
                .description("Test")
                .build();

        PaymentResponseDTO response = PaymentResponseDTO.builder()
                .orderId(TEST_ORDER_ID)
                .paymentUrl(TEST_CONFIRM_URL)
                .status(PaymentStatus.PENDING)
                .build();

        when(yooKassaService.createPayment(any(PaymentRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(TEST_ORDER_ID))
                .andExpect(jsonPath("$.paymentUrl").value(TEST_CONFIRM_URL))
                .andExpect(jsonPath("$.status").value(response.getStatus().getStatus()));
    }
}