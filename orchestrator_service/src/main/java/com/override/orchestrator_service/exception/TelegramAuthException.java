package com.override.orchestrator_service.exception;

import org.springframework.http.HttpStatus;

public class TelegramAuthException extends BaseException {
    public TelegramAuthException(String message) {
        super(message);
    }

    public TelegramAuthException() {
        super();
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getErrorCode() {
        return "ORCHESTRA_TELEGRAM_VERIFY_FAILED";
    }
}
