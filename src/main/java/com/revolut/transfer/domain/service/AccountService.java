package com.revolut.transfer.domain.service;

import com.revolut.transfer.domain.exception.AccountServiceException;
import com.revolut.transfer.domain.model.Account;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AccountService {
    CompletableFuture<Account> create(String firstName, String lastName, float balance) throws AccountServiceException;

    CompletableFuture<Account> update(String id, Account account) throws AccountServiceException;

    default CompletableFuture<Account> create(String firstName, String lastName) throws AccountServiceException {
        return create(firstName, lastName, 0.0F);
    }

    CompletableFuture<Account> get(String id) throws AccountServiceException;

    CompletableFuture<List<Account>> getAll() throws AccountServiceException;
}
