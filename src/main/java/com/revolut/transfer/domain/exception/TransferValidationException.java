package com.revolut.transfer.domain.exception;

public class TransferValidationException extends Exception {
    public TransferValidationException(String message) {
        super(message);
    }

    public TransferValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
