package com.revolut.transfer.persistence.service;

import com.revolut.transfer.domain.exception.AccountStorageException;
import com.revolut.transfer.domain.model.Account;
import com.revolut.transfer.domain.model.AccountFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.revolut.transfer.domain.model.AccountFixture.getAccount;
import static org.junit.jupiter.api.Assertions.*;

class ConcurrentHashMapAccountStorageTest {
    private static ConcurrentHashMapAccountStorage storage;

    @BeforeEach
    void setUp() {
        storage = new ConcurrentHashMapAccountStorage(new ConcurrentHashMap<>());
    }

    @Test
    void generateValidIds() {
        long totalUniqueValidIds = IntStream.range(0, 100)
                .boxed().parallel()
                .map(s -> storage.generateId())
                .distinct()
                .count();

        assertEquals(100L, totalUniqueValidIds);
    }

    @Test
    void createAccountSuccessfully() throws AccountStorageException {
        Account account = storage.create(getAccount("John", "Doe"));

        assertNotNull(account);
        assertNotEquals("664664", account.getId());
        assertEquals(9, account.getId().length());
        assertEquals(0, account.getVersion().get());
    }

    @Test
    void findAccountByIdSuccessfully() throws AccountStorageException {
        Account account = storage.create(getAccount("John", "Doe"));
        Account retrievedAccount = storage.findAccountById(account.getId());

        assertEquals(account, retrievedAccount);
    }

    @Test
    void findAccountByIdShouldReturnWithError() {
        assertThrows(AccountStorageException.class, () -> storage.findAccountById("001122333"));
    }

    @Test
    void findAllSuccessfully() throws AccountStorageException {
        storage.create(getAccount("John", "Doe"));
        storage.create(getAccount("John2", "Revolut"));

        List<Account> accounts = storage.findAll();

        assertEquals(2, accounts.size());
    }

    @Test
    void findAllShouldReturnWithError() throws AccountStorageException {
        assertTrue(storage.findAll().isEmpty());
    }

    @Test
    void updateAccountSuccessfully() throws AccountStorageException {
        float balance = 50.00F;
        Account account = storage.create(getAccount("John", "Doe"));

        Integer versionBefore = account.getVersion().get();
        account.setBalance(balance);

        Account updatedAccount = storage.update(account);
        Integer versionAfter = updatedAccount.getVersion().get();

        assertNotEquals(versionBefore, versionAfter);
        assertEquals(50.00F, updatedAccount.getBalance());
    }

    @Test
    void updateNonExistingAccount() {
        assertThrows(AccountStorageException.class, () ->
                storage.update(AccountFixture.getAccount("johndoe", "doe"))
        );
    }

    @Test
    void updateWithOutdatedAccount() throws AccountStorageException {
        Account account = storage.create(getAccount("John", "Doe"));
        storage.update(account);

        Account account1 = getAccount("John", "Doe");
        account1.setId(account.getId());
        account1.setVersion(new AtomicInteger(0));

        assertThrows(AccountStorageException.class, () -> storage.update(account1));
    }
}