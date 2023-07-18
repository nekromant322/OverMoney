package com.override.orchestrator_service.exception;

import org.springframework.http.HttpStatus;

public class XLSXProcessingException extends BaseException {
    public XLSXProcessingException() {
        super();
    }

    public XLSXProcessingException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getErrorCode() {
        return "ORCHESTRA_PARSING_XLSX_FAILED";
    }
}
