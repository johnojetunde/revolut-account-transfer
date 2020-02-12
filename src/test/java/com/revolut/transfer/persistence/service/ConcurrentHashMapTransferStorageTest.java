package com.revolut.transfer.persistence.service;

import com.revolut.transfer.domain.exception.TransferStorageException;
import com.revolut.transfer.domain.model.Transfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import static com.revolut.transfer.domain.model.TransferFixture.getTransfer;
import static org.junit.jupiter.api.Assertions.*;

class ConcurrentHashMapTransferStorageTest {
    private static ConcurrentHashMapTransferStorage storage;

    @BeforeEach
    void setUp() {
        storage = new ConcurrentHashMapTransferStorage(new ConcurrentHashMap<>());
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
    void createTransferSuccessfully() throws TransferStorageException {
        Transfer transfer = storage.create(getTransfer("John", "Doe"));

        assertAll("verifying balance",
                () -> assertNotNull(transfer),
                () -> assertNotEquals("id", transfer.getId()),
                () -> assertEquals("John", transfer.getSenderAccountId()),
                () -> assertEquals("Doe", transfer.getReceiverAccountId()));
    }

    @Test
    void findTransferByIdSuccessfully() throws TransferStorageException {
        Transfer transfer = storage.create(getTransfer("John", "Doe"));
        Transfer transferLog = storage.findById(transfer.getId());

        assertEquals(transfer, transferLog);
    }

    @Test
    void findTransferByIdShouldReturnWithError() {
        assertThrows(TransferStorageException.class, () -> storage.findById("001122333"));
    }

    @Test
    void findAllSuccessfully() throws TransferStorageException {
        storage.create(getTransfer("John", "Doe"));
        storage.create(getTransfer("John2", "Revolut"));

        List<Transfer> transfers = storage.findAll();

        assertEquals(2, transfers.size());
    }

    @Test
    void findAllShouldReturnWithError() throws TransferStorageException {
        assertTrue(storage.findAll().isEmpty());
    }
}