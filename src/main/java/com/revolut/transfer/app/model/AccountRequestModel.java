package com.revolut.transfer.app.model;

import com.revolut.transfer.domain.model.Account;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class AccountRequestModel {
    @NotBlank(message = "Firstname is required")
    private String firstname;
    @NotBlank(message = "Lastname is required")
    private String lastname;
    @Min(value = 0, message = "Minimum balance is 0")
    private float balance = 0;

    public Account toAccount() {
        return Account.builder()
                .balance(this.balance)
                .firstname(this.firstname)
                .lastname(this.lastname)
                .build();
    }
}
