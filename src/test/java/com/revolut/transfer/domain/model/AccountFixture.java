package com.revolut.transfer.domain.model;

import lombok.experimental.UtilityClass;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static java.math.BigDecimal.ZERO;

@UtilityClass
public class AccountFixture {
    public static Account getAccount(String firstname, String lastname) {
        return Account.builder()
                .id("664664")
                .firstname(firstname)
                .lastname(lastname)
                .balance(new AtomicReference<>(ZERO))
                .version(new AtomicInteger(20))
                .build();
    }
}
