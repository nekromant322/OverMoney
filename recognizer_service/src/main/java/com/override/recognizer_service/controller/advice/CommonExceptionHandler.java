package com.override.recognizer_service.controller.advice;

import com.override.dto.CommonErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<CommonErrorDTO> handleGenericException(Exception e) {
        log.error("RECOGNIZER_SERVICE_UNEXPECTED_EXCEPTION", e);
        String errorMessage = "Внутренняя ошибка сервера. Пожалуйста, повторите попытку позже.";
        return createErrorResponse("INTERNAL_SERVER_ERROR", errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<CommonErrorDTO> createErrorResponse(String errorCode, String message
            , HttpStatus httpStatus) {
        CommonErrorDTO errorDTO = CommonErrorDTO.builder()
                .code(errorCode)
                .message(message)
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(httpStatus).body(errorDTO);
    }
}
