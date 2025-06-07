package com.override.payment_service.controller.rest;

import com.override.dto.PaymentRequestDTO;
import com.override.dto.PaymentResponseDTO;
import com.override.payment_service.service.YooKassaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private YooKassaService yooKassaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreatePayment() throws Exception {
        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setOrderId("abc");
        request.setAmount(BigDecimal.valueOf(123));
        request.setCurrency("RUB");
        request.setReturnUrl("https://return.url");
        request.setDescription("Test");

        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setOrderId("abc");
        response.setPaymentUrl("https://confirm.url");
        response.setStatus("pending");

        when(yooKassaService.createPayment(any())).thenReturn(response);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("abc"))
                .andExpect(jsonPath("$.paymentUrl").value("https://confirm.url"))
                .andExpect(jsonPath("$.status").value("pending"));
    }
}