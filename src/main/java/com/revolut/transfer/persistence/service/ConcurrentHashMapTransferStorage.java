package com.revolut.transfer.persistence.service;

import com.revolut.transfer.domain.exception.TransferStorageException;
import com.revolut.transfer.domain.model.Transfer;
import com.revolut.transfer.domain.service.TransferStorage;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static com.revolut.transfer.domain.util.FunctionUtils.getAllAsList;
import static java.lang.String.valueOf;

@AllArgsConstructor
public class ConcurrentHashMapTransferStorage implements TransferStorage {
    private final ConcurrentHashMap<String, Transfer> database;

    @Override
    public Transfer create(Transfer transfer) throws TransferStorageException {
        try {
            String id = valueOf(generateId());
            transfer.setId(id);

            database.put(id, transfer);

            return database.get(id);
        } catch (Exception e) {
            throw new TransferStorageException("Unable to create transfer record");
        }
    }

    @Override
    public Transfer findById(String id) throws TransferStorageException {
        var transfer = database.get(id);
        if (transfer == null) {
            throw new TransferStorageException("Transfer record does not exist");
        }
        return transfer;
    }

    @Override
    public List<Transfer> findAll() throws TransferStorageException {
        return getAllAsList(database);
    }

    synchronized String generateId() {
        Random random = new Random(System.nanoTime());
        return valueOf(random.nextInt(1000000));
    }
}
