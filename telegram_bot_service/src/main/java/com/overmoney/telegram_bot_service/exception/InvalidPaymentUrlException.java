package com.overmoney.telegram_bot_service.exception;

public class InvalidPaymentUrlException extends Exception {
    public InvalidPaymentUrlException(String message) {
        super(message);
    }
}