package com.revolut.transfer.domain.service;

import com.revolut.transfer.domain.exception.TransferStorageException;
import com.revolut.transfer.domain.model.Transfer;

import java.util.List;

public interface TransferStorage {
    Transfer create(Transfer transfer) throws TransferStorageException;

    Transfer get(String id) throws TransferStorageException;

    List<Transfer> getAll() throws TransferStorageException;
}
