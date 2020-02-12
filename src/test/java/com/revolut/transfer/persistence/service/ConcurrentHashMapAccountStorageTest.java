package com.revolut.transfer.persistence.service;

import com.revolut.transfer.domain.exception.AccountStorageException;
import com.revolut.transfer.domain.model.Account;
import com.revolut.transfer.domain.model.AccountFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
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

        assertAll("verifying balance",
                () -> assertNotNull(account),
                () -> assertNotEquals("664664", account.getId()),
                () -> assertTrue(account.getId().contains("REV")),
                () -> assertEquals(0, account.getVersion().getAcquire()));
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
        BigDecimal balance = new BigDecimal("50.0");
        Account account = storage.create(getAccount("John", "Doe"));

        Integer versionBefore = account.getVersion().getAcquire();
        account.setBalance(new AtomicReference<>(balance));

        Account updatedAccount = storage.update(account);
        Integer versionAfter = updatedAccount.getVersion().getAcquire();

        assertNotEquals(versionBefore, versionAfter);
        assertEquals(50.0, updatedAccount.getBalance().getAcquire().doubleValue());
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