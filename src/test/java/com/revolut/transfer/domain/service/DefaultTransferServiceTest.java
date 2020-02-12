package com.revolut.transfer.domain.service;

import com.revolut.transfer.domain.exception.AccountServiceException;
import com.revolut.transfer.domain.exception.TransferServiceException;
import com.revolut.transfer.domain.model.Account;
import com.revolut.transfer.domain.model.Transfer;
import com.revolut.transfer.persistence.service.ConcurrentHashMapAccountStorage;
import com.revolut.transfer.persistence.service.ConcurrentHashMapTransferStorage;
import lombok.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

class DefaultTransferServiceTest {
    private DefaultTransferService transferService;
    private DefaultAccountService accountService;

    @BeforeEach
    void setUp() {
        TransferStorage transferStorage = new ConcurrentHashMapTransferStorage(new ConcurrentHashMap<>());
        AccountStorage accountStorage = new ConcurrentHashMapAccountStorage(new ConcurrentHashMap<>());
        List<TransferValidator> validators = singletonList(new BalanceTransferValidator());

        accountService = new DefaultAccountService(accountStorage);
        transferService = new DefaultTransferService(transferStorage, accountStorage, validators);
    }

    @Test
    void createTransferSuccessfully() {
        try {
            TransferResult result = createTransfer();
            Transfer transfer = result.transfer;
            Account sender = accountService.get(result.sender.getId()).get();
            Account receiver = accountService.get(result.receiver.getId()).get();

            assertAll("verifying balance",
                    () -> assertNotNull(transfer),
                    () -> assertEquals(80.0, sender.getBalance().getAcquire().doubleValue()),
                    () -> assertEquals(50.0, receiver.getBalance().getAcquire().doubleValue()));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void insufficientBalanceException() {
        try {
            CompletableFuture<Account> account1 = accountService.create("Jayeola", "Ginger", new BigDecimal("10"));
            CompletableFuture<Account> account2 = accountService.create("Jayeola", "Ginger", new BigDecimal("30"));
            Account sender = account1.get();
            Account receiver = account2.get();

            Transfer transfer = Transfer.builder()
                    .amount(new BigDecimal(20))
                    .senderAccountId(sender.getId())
                    .receiverAccountId(receiver.getId()).build();

            transferService.transfer(transfer);
            fail();
        } catch (Exception e) {
            assertEquals("Insufficient balance to make this transfer", e.getMessage());
        }
    }

    @Test
    void getTransferRecordSuccessfully() {
        try {
            String id = createTransfer().transfer.getId();
            Transfer result = transferService.get(id).get();

            assertNotNull(result);
            assertEquals(20.0, result.getAmount().doubleValue());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void getAllTransferRecordsSuccessfully() {
        try {
            Transfer transfer = createTransfer().transfer;
            transferService.transfer(transfer);
            List<Transfer> result = transferService.getAll().get();

            assertAll("verifying balance",
                    () -> assertNotNull(result),
                    () -> assertEquals(2, result.size()),
                    () -> assertEquals(20.0, result.get(0).getAmount().doubleValue()));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void exceptionWhenTransferring() {
        assertThrows(TransferServiceException.class,
                () -> {
                    Transfer transfer = Transfer.builder()
                            .amount(new BigDecimal(20))
                            .senderAccountId("1234566")
                            .receiverAccountId("uhdgfgfgfhfj").build();
                    transferService.transfer(transfer);
                });
    }

    @Test
    void exceptionWhenGettingTransferById() {
        try {
            transferService.get("funny id");
            fail();
        } catch (TransferServiceException e) {
            assertEquals("Unable to retrieve transfer record", e.getMessage());
        }
    }

    private TransferResult createTransfer() throws AccountServiceException, InterruptedException, java.util.concurrent.ExecutionException, TransferServiceException {
        CompletableFuture<Account> account1 = accountService.create("Jayeola", "Ginger", new BigDecimal("100"));
        CompletableFuture<Account> account2 = accountService.create("Jayeola", "Ginger", new BigDecimal("30"));
        Account sender = account1.get();
        Account receiver = account2.get();

        Transfer transfer = Transfer.builder()
                .amount(new BigDecimal(20))
                .senderAccountId(sender.getId())
                .receiverAccountId(receiver.getId()).build();

        return new TransferResult(transferService.transfer(transfer).get(), sender, receiver);
    }

    @Value
    static class TransferResult {
        private Transfer transfer;
        private Account sender;
        private Account receiver;
    }
}