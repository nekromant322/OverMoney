package com.override.orchestrator_service.controller.advice;

import com.override.dto.CommonErrorDTO;
import com.override.orchestrator_service.exception.BaseException;
import com.override.orchestrator_service.exception.RequestSizeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Instant;

@ControllerAdvice
@Slf4j
public class CommonExceptionHandler {
    private final String INTERNAL_SERVER_ERROR_CODE = "ORCHESTRA_UNEXPECTED";
    private final int INTERNAL_SERVER_STATUS_CODE = 500;
    @Value("${request.max-size}")
    private String maxSizeRequest;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> test(MaxUploadSizeExceededException e) {
        return handleException(new RequestSizeException("Размер файла не должен превышать " + maxSizeRequest));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error(e.getMessage(), e);
        String errorCode = getErrorCode(e);
        int statusCode = getHttpStatusCode(e);
        return ResponseEntity.status(statusCode).body(createDto(errorCode, e.getMessage()));
    }

    private String getErrorCode(Exception exception) {
        if (exception instanceof BaseException) {
            return ((BaseException) exception).getErrorCode();
        }
        return INTERNAL_SERVER_ERROR_CODE;
    }

    private int getHttpStatusCode(Exception exception) {
        if (exception instanceof BaseException) {
            return ((BaseException) exception).getStatusCode();
        }
        return INTERNAL_SERVER_STATUS_CODE;
    }

    private CommonErrorDTO createDto(String errorCode, String errorMessage) {
        return CommonErrorDTO
                .builder()
                .code(errorCode)
                .message(errorMessage)
                .timestamp(Instant.now())
                .build();
    }
}
