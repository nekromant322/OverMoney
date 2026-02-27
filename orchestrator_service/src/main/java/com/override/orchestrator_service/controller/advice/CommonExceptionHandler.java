package com.override.orchestrator_service.controller.advice;

import com.override.dto.CommonErrorDTO;
import com.override.orchestrator_service.exception.BaseException;
import com.override.orchestrator_service.exception.InvalidDataException;
import com.override.orchestrator_service.exception.RequestSizeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Instant;

@ControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    @Value("${spring.servlet.multipart.max-request-size}")
    private String maxSizeRequest;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> test(MaxUploadSizeExceededException e) {
        String errorMessage = String.format("The file size must not exceed %s", maxSizeRequest);
        log.error(errorMessage, e);
        return handleException(new RequestSizeException(errorMessage));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<CommonErrorDTO> handleBaseException(BaseException e) {
        log.error("Handled BaseException: ", e);
        return createErrorResponse(e, HttpStatus.valueOf(e.getStatusCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonErrorDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Handled MethodArgumentNotValidException: ", e);
        StringBuilder errorMessage = new StringBuilder("Validation failed:\n ");
        e.getBindingResult().getFieldErrors().forEach(fieldError ->
                errorMessage.append(fieldError.getField())
                        .append(" - ")
                        .append(fieldError.getDefaultMessage())
                        .append(";\n")
        );
        InvalidDataException invalidDataException = new InvalidDataException(errorMessage.toString());
        return createErrorResponse(invalidDataException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<CommonErrorDTO> handleException(Exception e) {
        log.error("Unhandled exception: ", e);
        return createErrorResponse(new BaseException("Internal server error"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<CommonErrorDTO> createErrorResponse(BaseException e, HttpStatus httpStatus) {
        CommonErrorDTO errorDTO = CommonErrorDTO
                .builder()
                .code(e.getErrorCode())
                .message(e.getMessage())
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(httpStatus).body(errorDTO);
    }
}
