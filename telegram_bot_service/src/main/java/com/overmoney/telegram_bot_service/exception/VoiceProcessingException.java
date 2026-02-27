package com.overmoney.telegram_bot_service.exception;

public class VoiceProcessingException extends RuntimeException {
    public VoiceProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
