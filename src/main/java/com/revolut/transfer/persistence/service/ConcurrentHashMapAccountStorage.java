package com.revolut.transfer.persistence.service;

import com.revolut.transfer.domain.exception.AccountStorageException;
import com.revolut.transfer.domain.model.Account;
import com.revolut.transfer.domain.service.AccountStorage;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.revolut.transfer.domain.util.FunctionUtil.getAllAsList;
import static java.lang.String.format;
import static java.lang.String.valueOf;

@AllArgsConstructor
public class ConcurrentHashMapAccountStorage implements AccountStorage {
    private static final String PREFIX = "REV";
    private static final int ID_GEN_BOUND = 1000000000;
    private final ConcurrentHashMap<String, Account> database;

    @Override
    public Account create(Account account) throws AccountStorageException {
        try {
            String id = generateId();
            account.setId(id);
            account.setVersion(new AtomicInteger(0));

            database.put(id, account);

            return database.get(id);
        } catch (Exception e) {
            throw new AccountStorageException("Unable to create account");
        }
    }

    synchronized String generateId() {
        Random random = new Random(System.nanoTime());
        return PREFIX.concat(valueOf(random.nextInt(ID_GEN_BOUND)));
    }

    @Override
    public Account findAccountById(String id) throws AccountStorageException {
        var account = database.get(id);
        if (account == null) {
            throw new AccountStorageException(format("Account (%s) does not exist", id));
        }
        return account;
    }

    @Override
    public List<Account> findAll() throws AccountStorageException {
        try {
            return getAllAsList(database);
        } catch (Exception e) {
            throw new AccountStorageException("Unable to retrieve all accounts", e);
        }
    }

    @Override
    public Account update(Account account) throws AccountStorageException {
        Account existingAccount = findAccountById(account.getId());
        validateVersion(existingAccount, account);
        updateVersion(account);

        return database.replace(account.getId(), account);
    }

    private void validateVersion(Account currentAccountValue, Account newAccountValue) throws AccountStorageException {
        if (currentAccountValue.getVersion().getAcquire() > newAccountValue.getVersion().getAcquire()) {
            throw new AccountStorageException("Account has been updated by another process");
        }
    }

    private void updateVersion(Account account) {
        AtomicInteger version = account.getVersion();
        version.getAndAdd(1);
        account.setVersion(version);
    }

}
