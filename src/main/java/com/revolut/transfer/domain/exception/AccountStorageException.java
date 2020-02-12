package com.revolut.transfer.domain.exception;

public class AccountStorageException extends Exception {
    public AccountStorageException(String message) {
        super(message);
    }

    public AccountStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
