package com.override.orchestrator_service.controller.advice;

import com.override.dto.CommonErrorDTO;
import com.override.orchestrator_service.exception.CategoryNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.management.InstanceNotFoundException;
import javax.naming.AuthenticationException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import static com.override.orchestrator_service.controller.advice.OrchestractorError.*;

@ControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error(e.getMessage(), e);
        OrchestractorError error = getError(e);
        return ResponseEntity.status(error.getHttpStatus()).body(createDto(error, e));
    }

    private OrchestractorError getError(Exception exception) {
        if (exception instanceof InstanceNotFoundException) {
            return ORCHESTRA_INSTANCE_NOT_FOUND;
        }
        if (exception instanceof AuthenticationException) {
            return ORCHESTRA_INVALID_TOKEN;
        }
        if (exception instanceof NoSuchAlgorithmException) {
            return ORCHESTRA_TELEGRAM_VERIFY_FAILED;
        }
        if (exception instanceof InvalidKeyException) {
            return ORCHESTRA_TELEGRAM_VERIFY_FAILED;
        }
        if (exception instanceof CategoryNotFoundException) {
            return ORCHESTRA_CATEGORY_NOT_FOUND;
        }
        return ORCHESTRA_UNEXPECTED;
    }

    private CommonErrorDTO createDto(OrchestractorError error, Exception ex) {
        return CommonErrorDTO
                .builder()
                .code(error.name())
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .build();
    }
}
