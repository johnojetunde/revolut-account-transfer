package com.revolut.transfer.app.model;

import com.revolut.transfer.domain.model.Transfer;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class TransferResponseModel {
    private String id;
    private BigDecimal amount;
    private String senderAccountId;
    private String receiverAccountId;

    public static TransferResponseModel fromModel(Transfer transfer) {
        return TransferResponseModel.builder()
                .id(transfer.getId())
                .amount(transfer.getAmount())
                .senderAccountId(transfer.getSenderAccountId())
                .receiverAccountId(transfer.getReceiverAccountId())
                .build();
    }
}
