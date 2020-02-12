package com.revolut.transfer.app.exception;

public class RequestValidationException extends Exception {
    public RequestValidationException(String message) {
        super(message);
    }
}
