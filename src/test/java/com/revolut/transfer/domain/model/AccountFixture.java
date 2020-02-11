package com.revolut.transfer.domain.model;

import lombok.experimental.UtilityClass;

import java.util.concurrent.atomic.AtomicInteger;

@UtilityClass
public class AccountFixture {
    public static Account getAccount(String firstname, String lastname) {
        return Account.builder()
                .id("664664")
                .firstname(firstname)
                .lastname(lastname)
                .version(new AtomicInteger(20))
                .build();
    }
}
