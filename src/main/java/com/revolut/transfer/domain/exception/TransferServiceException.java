package com.revolut.transfer.domain.exception;

public class TransferServiceException extends Exception {
    public TransferServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
