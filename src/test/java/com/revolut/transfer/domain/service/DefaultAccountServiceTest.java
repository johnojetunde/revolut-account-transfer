package com.revolut.transfer.domain.service;

import com.revolut.transfer.domain.exception.AccountServiceException;
import com.revolut.transfer.domain.model.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.revolut.transfer.domain.model.AccountFixture.getAccount;
import static com.revolut.transfer.domain.stubs.AccountStorageStub.accountStorageWithErrors;
import static com.revolut.transfer.domain.stubs.AccountStorageStub.validAccountStorage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class DefaultAccountServiceTest {
    private DefaultAccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new DefaultAccountService(validAccountStorage("johndoe", "revolut"));
    }

    @Test
    void createAccountSuccessfully() {
        try {
            CompletableFuture<Account> result = accountService.create("Jayeola", "Ginger");
            Account account = result.get();

            assertEquals("664664", account.getId());
            assertEquals("johndoe", account.getFirstname());
            assertEquals("revolut", account.getLastname());
            assertEquals(20, account.getVersion().get());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void exceptionWhenCreatingAccount() {
        accountService = new DefaultAccountService(accountStorageWithErrors());

        Assertions.assertThrows(AccountServiceException.class,
                () -> accountService.create("Jayeola", "Ginger"));
    }

    @Test
    void getAccountSuccessfully() {
        try {
            CompletableFuture<Account> result = accountService.get("0001");
            Account account = result.get();

            assertEquals("0001", account.getId());
            assertEquals("johndoe", account.getFirstname());
            assertEquals("revolut", account.getLastname());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void exceptionWhenGettingAccount() {
        accountService = new DefaultAccountService(accountStorageWithErrors());
        Assertions.assertThrows(AccountServiceException.class,
                () -> accountService.get("122323"), "");
    }

    @Test
    void getAllAccountsSuccessfully() {
        try {
            CompletableFuture<List<Account>> result = accountService.getAll();
            List<Account> accounts = result.get();

            assertEquals(1, accounts.size());
            assertEquals("664664", accounts.get(0).getId());
            assertEquals("johndoe", accounts.get(0).getFirstname());
            assertEquals("revolut", accounts.get(0).getLastname());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void exceptionWhenGettingAllAccounts() {
        accountService = new DefaultAccountService(accountStorageWithErrors());
        Assertions.assertThrows(AccountServiceException.class,
                () -> accountService.getAll(), "");
    }

    @Test
    void updateAccountsSuccessfully() {
        try {
            CompletableFuture<Account> result1 = accountService.create("Jayeola", "Ginger");
            Account account = result1.get();

            CompletableFuture<Account> result = accountService.update(account.getId(), account);
            Account updatedAccount = result.get();

            assertEquals("664664", updatedAccount.getId());
            assertEquals("johndoe", updatedAccount.getFirstname());
            assertEquals("revolut", updatedAccount.getLastname());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void exceptionWhenUpdatingAccounts() {
        accountService = new DefaultAccountService(accountStorageWithErrors());
        Assertions.assertThrows(AccountServiceException.class,
                () -> accountService.update("1234", getAccount("John", "Dami")), "");
    }
}