package com.revolut.transfer.domain.service;

import com.revolut.transfer.domain.exception.AccountStorageException;
import com.revolut.transfer.domain.model.Account;

import java.util.List;

public interface AccountStorage {
    Account create(Account account) throws AccountStorageException;

    Account findAccountById(String id) throws AccountStorageException;

    List<Account> findAll() throws AccountStorageException;

    Account update(Account account) throws AccountStorageException;
}
