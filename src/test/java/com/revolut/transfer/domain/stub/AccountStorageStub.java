package com.revolut.transfer.domain.stub;

import com.revolut.transfer.domain.exception.AccountStorageException;
import com.revolut.transfer.domain.model.Account;
import com.revolut.transfer.domain.service.AccountStorage;

import java.util.List;

import static com.revolut.transfer.domain.model.AccountFixture.getAccount;
import static java.util.Collections.singletonList;

public class AccountStorageStub {
    public static AccountStorage validAccountStorage(String firstname, String lastname) {
        return new AccountStorage() {
            @Override
            public Account create(Account account) throws AccountStorageException {
                return getAccount(firstname, lastname);
            }

            @Override
            public Account findAccountById(String id) throws AccountStorageException {
                Account account = getAccount(firstname, lastname);
                account.setId(id);
                return account;
            }

            @Override
            public List<Account> findAll() throws AccountStorageException {
                return singletonList(getAccount(firstname, lastname));
            }

            @Override
            public Account update(Account account) throws AccountStorageException {
                return getAccount(firstname, lastname);
            }
        };
    }

    public static AccountStorage accountStorageWithErrors() {
        return new AccountStorage() {
            @Override
            public Account create(Account account) throws AccountStorageException {
                throw new AccountStorageException("Error creating account");
            }

            @Override
            public Account findAccountById(String id) throws AccountStorageException {
                throw new AccountStorageException("Error retrieving account");
            }

            @Override
            public List<Account> findAll() throws AccountStorageException {
                throw new AccountStorageException("Error retrieving all accounts");
            }

            @Override
            public Account update(Account account) throws AccountStorageException {
                throw new AccountStorageException("Error updating account");
            }
        };
    }
}
