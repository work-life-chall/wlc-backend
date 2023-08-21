package com.bonfire.challenge.validation;

import lombok.Data;

@Data
public class ExceptionResponse {
    private boolean success;
    private String message;
    private Object data;

    public ExceptionResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
