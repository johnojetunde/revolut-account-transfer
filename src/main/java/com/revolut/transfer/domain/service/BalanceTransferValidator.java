package com.revolut.transfer.domain.service;

import com.revolut.transfer.domain.exception.TransferValidationException;
import com.revolut.transfer.domain.model.Account;
import com.revolut.transfer.domain.model.Transfer;

import java.math.BigDecimal;

public class BalanceTransferValidator implements TransferValidator {
    @Override
    public void validate(Account sender, Account receiver, Transfer transfer) throws TransferValidationException {
        BigDecimal senderBalance = sender.getBalance().getAcquire();

        if (senderBalance.compareTo(transfer.getAmount()) < 0) {
            throw new TransferValidationException("Insufficient balance to make this transfer");
        }
    }
}
