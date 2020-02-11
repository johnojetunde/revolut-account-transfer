package com.revolut.transfer.app.model;

import com.revolut.transfer.domain.model.Account;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class AccountResponseModel {
    private String id;
    private int version;
    private BigDecimal balance;
    private String firstname;
    private String lastname;

    public static AccountResponseModel fromAccount(Account account) {
        return new AccountResponseModelBuilder()
                .id(account.getId())
                .version(account.getVersion().getAcquire())
                .balance(account.getBalance().getAcquire())
                .firstname(account.getFirstname())
                .lastname(account.getLastname())
                .build();
    }
}
