package com.revolut.transfer.persistence.service;

import com.revolut.transfer.domain.exception.AccountStorageException;
import com.revolut.transfer.domain.model.Account;
import com.revolut.transfer.domain.service.AccountStorage;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ConcurrentHashMapAccountStorage implements AccountStorage {
    private final ConcurrentHashMap<String, Account> database;

    @Override
    public Account create(Account account) throws AccountStorageException {
        try {
            String id = String.valueOf(generateId());
            account.setId(id);
            account.setVersion(new AtomicInteger(0));

            database.put(id, account);

            return database.get(id);
        } catch (Exception e) {
            throw new AccountStorageException("Unable to create account");
        }
    }

    @Override
    public Account findAccountById(String id) throws AccountStorageException {
        var account = database.get(id);
        if (account == null) {
            throw new AccountStorageException("Account does not exist");
        }
        return account;
    }

    @Override
    public List<Account> findAll() throws AccountStorageException {
        return database.entrySet()
                .parallelStream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public Account update(Account account) throws AccountStorageException {
        Account existingAccount = findAccountById(account.getId());
        validateVersion(existingAccount, account);
        updateVersion(account);

        return database.replace(account.getId(), account);
    }

    private void validateVersion(Account currentAccountValue, Account newAccountValue) throws AccountStorageException {
        if (currentAccountValue.getVersion().get() > newAccountValue.getVersion().get()) {
            throw new AccountStorageException("Account has been updated by another process");
        }
    }

    private void updateVersion(Account account) {
        AtomicInteger version = account.getVersion();
        version.getAndAdd(1);
        account.setVersion(version);

    }

    synchronized int generateId() {
        Random random = new Random(System.nanoTime());
        return random.nextInt(1000000000);
    }
}
