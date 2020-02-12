package com.revolut.transfer.app.model;

import com.revolut.transfer.domain.model.Transfer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransferRequestModel {
    private BigDecimal amount;
    private String senderAccountId;
    private String receiverAccountId;

    public Transfer toModel() {
        return Transfer.builder()
                .amount(this.amount)
                .senderAccountId(this.senderAccountId)
                .receiverAccountId(this.receiverAccountId)
                .build();
    }
}
