package com.revolut.transfer.app.model;

import com.revolut.transfer.domain.model.Account;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AccountResponseModel {
    private String id;
    private int version;
    private float balance;
    private String firstname;
    private String lastname;

    public static AccountResponseModel fromAccount(Account account) {
        return new AccountResponseModelBuilder()
                .id(account.getId())
                .version(account.getVersion().get())
                .balance(account.getBalance())
                .firstname(account.getFirstname())
                .lastname(account.getLastname())
                .build();
    }
}