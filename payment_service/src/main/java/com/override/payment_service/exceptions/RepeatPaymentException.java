package com.override.payment_service.exceptions;

public class RepeatPaymentException extends RuntimeException {
    public RepeatPaymentException(String message) {
        super(message);
    }
}
