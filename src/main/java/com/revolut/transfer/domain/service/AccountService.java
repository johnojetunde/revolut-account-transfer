package com.revolut.transfer.domain.service;

import com.revolut.transfer.domain.exception.AccountServiceException;
import com.revolut.transfer.domain.model.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.math.BigDecimal.ZERO;

public interface AccountService {
    CompletableFuture<Account> create(String firstName, String lastName, BigDecimal balance) throws AccountServiceException;

    default CompletableFuture<Account> create(String firstName, String lastName) throws AccountServiceException {
        return create(firstName, lastName, ZERO);
    }

    CompletableFuture<Account> get(String id) throws AccountServiceException;

    CompletableFuture<List<Account>> getAll() throws AccountServiceException;

    CompletableFuture<Account> update(String id, Account account) throws AccountServiceException;
}
