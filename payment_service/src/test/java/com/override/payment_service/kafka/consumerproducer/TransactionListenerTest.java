package com.override.payment_service.kafka.consumerproducer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionListenerTest {

    @Test
    void testSetAndGetProcessing() {
        TransactionListener listener = new TransactionListener();
        listener.setProcessing("test-processing");

        assertThat(listener.getProcessing()).isEqualTo("test-processing");
    }
}