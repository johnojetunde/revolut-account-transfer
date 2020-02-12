package com.revolut.transfer.domain.service;

import com.revolut.transfer.domain.exception.TransferValidationException;
import com.revolut.transfer.domain.model.Account;
import com.revolut.transfer.domain.model.Transfer;

public interface TransferValidator {
    void validate(Account sender, Account receiver, Transfer transfer) throws TransferValidationException;
}
