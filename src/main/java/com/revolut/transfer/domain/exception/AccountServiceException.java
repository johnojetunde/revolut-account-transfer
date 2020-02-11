package com.revolut.transfer.domain.exception;

public class AccountServiceException extends Exception {
    public AccountServiceException(String message, Throwable e) {
        super(message, e);
    }
}
