package com.bonfire.challenge.validation;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ValidateExceptionHandler {

    @ExceptionHandler(ValidateException.class)
    public ExceptionResponse validateException (ValidateException e) {
        return new ExceptionResponse(false, e.getMessage());
    }
}
