package com.revolut.transfer.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
@Builder
public class Account {
    private String id;
    private AtomicInteger version;
    private float balance = 0.0F;
    private String firstname;
    private String lastname;
}
