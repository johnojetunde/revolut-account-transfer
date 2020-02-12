package com.revolut.transfer.domain.service;

import com.revolut.transfer.domain.exception.TransferServiceException;
import com.revolut.transfer.domain.model.Transfer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TransferService {
    CompletableFuture<Transfer> transfer(Transfer transfer) throws TransferServiceException;

    CompletableFuture<Transfer> get(String id) throws TransferServiceException;

    CompletableFuture<List<Transfer>> getAll() throws TransferServiceException;
}
