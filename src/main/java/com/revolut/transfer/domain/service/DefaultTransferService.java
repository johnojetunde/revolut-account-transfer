package com.revolut.transfer.domain.service;

import com.revolut.transfer.domain.exception.*;
import com.revolut.transfer.domain.model.Account;
import com.revolut.transfer.domain.model.Transfer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@Slf4j
@AllArgsConstructor
public class DefaultTransferService implements TransferService {
    private TransferStorage transferStorage;
    private AccountStorage accountStorage;
    private List<TransferValidator> validators;
    private Account sender;
    private Account receiver;

    @Override
    public Transfer transfer(Transfer transfer) throws TransferServiceException {
        try {
            sender = accountStorage.findAccountById(transfer.getSenderAccountId());
            receiver = accountStorage.findAccountById(transfer.getReceiverAccountId());

            Account senderCopy = (Account) sender.clone();
            Account receiverCopy = (Account) sender.clone();

            for (TransferValidator validator : validators) {
                validator.validate(senderCopy, receiverCopy, transfer);
            }

            List<Account> accounts = moveMoneyAround(senderCopy, receiverCopy, transfer);

            updateAccounts(accounts);

            return transferStorage.create(transfer);
        } catch (CloneNotSupportedException | AccountStorageException | TransferStorageException e) {
            throw new TransferServiceException("Unable to complete transfer", e);
        } catch (TransferValidationException e) {
            throw new TransferServiceException(e.getMessage(), e);
        } catch (TransferSavingException e) {
            log.error("transfer failed. rolling back transaction now");
            rollbackTransaction();
            throw new TransferServiceException("Unable to complete transfer successful", e);
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

    private void updateAccounts(List<Account> accounts) throws TransferSavingException {
        try {
            updateMultipleAccount(accounts);
        } catch (AccountStorageException e) {
            throw new TransferSavingException("Unable to complete transfer", e);
        }
    }

    private void updateMultipleAccount(List<Account> accounts) throws AccountStorageException {
        for (Account account : accounts) {
            accountStorage.update(account);
        }
    }

    private void rollbackTransaction() {
        List<Account> accounts = asList(sender, receiver);
        accounts = accounts.stream().filter(Objects::nonNull).collect(Collectors.toList());
        try {
            updateMultipleAccount(accounts);
        } catch (AccountStorageException e) {
            log.error("Error trying to rollback transfers", e);
            //TODO: a better way of handling this rollback failures
        }
    }
}
