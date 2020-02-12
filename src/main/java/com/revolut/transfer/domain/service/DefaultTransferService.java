package com.revolut.transfer.domain.service;

import com.revolut.transfer.domain.exception.*;
import com.revolut.transfer.domain.model.Account;
import com.revolut.transfer.domain.model.Transfer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.concurrent.CompletableFuture.completedFuture;

@Slf4j
@AllArgsConstructor
public class DefaultTransferService implements TransferService {
    private TransferStorage transferStorage;
    private AccountStorage accountStorage;
    private List<TransferValidator> validators;

    @Override
    public CompletableFuture<Transfer> get(String id) throws TransferServiceException {
        try {
            return completedFuture(transferStorage.findById(id));
        } catch (TransferStorageException e) {
            throw new TransferServiceException("Unable to retrieve transfer record", e);
        }
    }

    @Override
    public CompletableFuture<List<Transfer>> getAll() throws TransferServiceException {
        try {
            return completedFuture(transferStorage.findAll());
        } catch (TransferStorageException e) {
            throw new TransferServiceException("Unable to retrieve all transfers", e);
        }
    }

    @Override
    public CompletableFuture<Transfer> transfer(Transfer transfer) throws TransferServiceException {
        return completedFuture(transferMoney(transfer));
    }

    private Transfer transferMoney(Transfer transfer) throws TransferServiceException {
        try {
            Account sender = retrieveAccount(transfer.getSenderAccountId());
            Account receiver = retrieveAccount(transfer.getReceiverAccountId());

            Account senderCopy = (Account) sender.clone();
            Account receiverCopy = (Account) receiver.clone();

            for (TransferValidator validator : validators) {
                validator.validate(senderCopy, receiverCopy, transfer);
            }

            List<Account> accounts = moveMoneyAround(senderCopy, receiverCopy, transfer);

            updateMultipleAccount(accounts);

            return transferStorage.create(transfer);
        } catch (CloneNotSupportedException | AccountStorageException | TransferStorageException e) {
            throw new TransferServiceException("Unable to complete transfer", e);
        } catch (TransferValidationException e) {
            throw new TransferServiceException(e.getMessage(), e);
        }
    }

    private Account retrieveAccount(String id) throws TransferValidationException {
        try {
            return accountStorage.findAccountById(id);
        } catch (AccountStorageException e) {
            throw new TransferValidationException(
                    format("Unable to find account associated with ID (%s)", id), e);
        }
    }

    private List<Account> moveMoneyAround(Account sender, Account receiver, Transfer transfer) {
        BigDecimal senderBalance = sender.getBalance().getAcquire();
        BigDecimal receiverBalance = receiver.getBalance().getAcquire();

        senderBalance = senderBalance.subtract(transfer.getAmount());
        receiverBalance = receiverBalance.add(transfer.getAmount());

        sender.setBalance(new AtomicReference<>(senderBalance));
        receiver.setBalance(new AtomicReference<>(receiverBalance));

        return asList(sender, receiver);
    }

    private void updateMultipleAccount(List<Account> accounts) throws AccountStorageException {
        //TODO: to implement a rollback if this fails
        for (Account account : accounts) {
            accountStorage.update(account);
        }
    }

}
