package com.revolut.transfer.domain.model;

import static java.math.BigDecimal.ZERO;

public class TransferFixture {
    public static Transfer getTransfer(String senderAccountId, String receiverAccountId) {
        return new Transfer("id", ZERO, senderAccountId, receiverAccountId);
    }
}
