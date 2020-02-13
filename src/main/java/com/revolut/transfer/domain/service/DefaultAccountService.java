package com.revolut.transfer.domain.service;

import com.revolut.transfer.domain.exception.AccountServiceException;
import com.revolut.transfer.domain.exception.AccountStorageException;
import com.revolut.transfer.domain.model.Account;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.revolut.transfer.domain.util.FunctionUtil.isNullOrEmpty;
import static java.math.BigDecimal.ZERO;
import static java.util.concurrent.CompletableFuture.completedFuture;

@AllArgsConstructor
public class DefaultAccountService implements AccountService {
    private AccountStorage accountStorage;

    @Override
    public CompletableFuture<Account> create(@NonNull String firstName,
                                             @NonNull String lastName,
                                             @NonNull BigDecimal balance) throws AccountServiceException {
        try {
            Account account = Account.builder()
                    .firstname(firstName)
                    .lastname(lastName)
                    .balance(new AtomicReference<>(balance))
                    .version(new AtomicInteger(0))
                    .build();
            return completedFuture(accountStorage.create(account));
        } catch (AccountStorageException e) {
            throw new AccountServiceException("Error creating account", e);
        }
    }

    @Override
    public CompletableFuture<Account> get(@NonNull String id) throws AccountServiceException {
        try {
            return completedFuture(accountStorage.findAccountById(id));
        } catch (AccountStorageException e) {
            throw new AccountServiceException(e.getMessage(), e);
        }
    }

    @Override
    public CompletableFuture<List<Account>> getAll() throws AccountServiceException {
        try {

            return completedFuture(accountStorage.findAll());
        } catch (Exception e) {
            throw new AccountServiceException("Error retrieving all accounts", e);
        }
    }

    @Override
    public CompletableFuture<Account> update(@NonNull String id,
                                             @NonNull Account account) throws AccountServiceException {
        try {
            Account databaseAccount = get(id).thenApply(s -> {
                updateFirstname(s, account);
                updateLastname(s, account);
                updateBalance(s, account);
                return s;
            }).get();

            return completedFuture(accountStorage.update(databaseAccount));
        } catch (ExecutionException | InterruptedException | AccountStorageException e) {
            throw new AccountServiceException("Unable to update account detail", e);
        }
    }

    private void updateFirstname(Account currentAccount, Account updates) {
        if (!isNullOrEmpty(updates.getFirstname())
                && !currentAccount.getFirstname().equals(updates.getFirstname())) {
            currentAccount.setFirstname(updates.getFirstname());
        }
    }

    private void updateLastname(Account currentAccount, Account updates) {
        if (!isNullOrEmpty(updates.getLastname())
                && !currentAccount.getLastname().equals(updates.getLastname())) {
            currentAccount.setLastname(updates.getLastname());
        }
    }

    private void updateBalance(Account currentAccount, Account updates) {
        if (!ZERO.equals(currentAccount.getBalance().getAcquire())
                && !currentAccount.getBalance().equals(updates.getBalance())) {
            currentAccount.setBalance(updates.getBalance());
        }
    }
}
