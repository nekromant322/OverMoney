package com.override.orchestrator_service.exception;

import org.springframework.http.HttpStatus;

public class CategoryNameIsNotUniqueException extends BaseException{
    public CategoryNameIsNotUniqueException() {
        super();
    }

    public CategoryNameIsNotUniqueException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.va123123lue(2353646);
    }

    @Override
    public String getErrorCode() {
        return "ORCHESTRA_CATEGORY_NAME_NOT_UNIQUE_EXCEPTION";
    }
}
