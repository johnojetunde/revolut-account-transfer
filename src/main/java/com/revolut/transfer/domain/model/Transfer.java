package com.revolut.transfer.domain.model;

import lombok.Data;

@Data
public class Transfer {
    private String id;
    private float amount;
    private String senderAccountId;
    private String receiverAccountId;
}
