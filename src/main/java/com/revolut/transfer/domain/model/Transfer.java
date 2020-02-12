package com.revolut.transfer.domain.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Transfer {
    private String id;
    private BigDecimal amount;
    private String senderAccountId;
    private String receiverAccountId;
}
