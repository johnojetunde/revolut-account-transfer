package com.revolut.transfer.domain.service;

import com.revolut.transfer.domain.exception.TransferServiceException;
import com.revolut.transfer.domain.model.Transfer;

public interface TransferService {
    Transfer transfer(Transfer transfer) throws TransferServiceException;
}
