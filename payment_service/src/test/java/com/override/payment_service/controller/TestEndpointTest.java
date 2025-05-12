package com.override.payment_service.controller;

import com.override.payment_service.controller.rest.TestEndpoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TestEndpoint.class)
class TestEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testEndpointShouldReturnTestString() throws Exception {
        mockMvc.perform(get("/api/payment/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Test endpoint"));
    }
}