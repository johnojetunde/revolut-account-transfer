package com.revolut.transfer.domain.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Data
@Builder
public class Account {
    private String id;
    private AtomicInteger version;
    private AtomicReference<BigDecimal> balance;
    private String firstname;
    private String lastname;
}
