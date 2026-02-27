package com.override.payment_service.exceptions;

public class SignatureNonMatchException extends RuntimeException {
    public SignatureNonMatchException(String message) {
        super(message);
    }
}
