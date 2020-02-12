package com.revolut.transfer.domain.exception;

public class TransferSavingException extends Exception {
    public TransferSavingException(String message, Throwable cause) {
        super(message, cause);
    }
}
