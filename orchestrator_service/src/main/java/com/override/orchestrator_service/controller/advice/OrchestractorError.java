package com.override.orchestrator_service.controller.advice;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OrchestractorError {
    ORCHESTRA_UNEXPECTED(HttpStatus.INTERNAL_SERVER_ERROR),
    ORCHESTRA_INVALID_TOKEN(HttpStatus.UNAUTHORIZED),
    ORCHESTRA_INSTANCE_NOT_FOUND(HttpStatus.BAD_REQUEST),
    ORCHESTRA_TELEGRAM_VERIFY_FAILED(HttpStatus.BAD_REQUEST),

    ORCHESTRA_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND);

    private HttpStatus httpStatus;

    OrchestractorError(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
