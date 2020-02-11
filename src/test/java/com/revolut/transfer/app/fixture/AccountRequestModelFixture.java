package com.revolut.transfer.app.fixture;

import com.revolut.transfer.app.model.AccountRequestModel;

import java.math.BigDecimal;

public class AccountRequestModelFixture {
    public static AccountRequestModel requestModelFixture(boolean valid) {
        String firstname = (valid) ? "Johndoe" : "";
        String lastname = (valid) ? "Revolut" : null;
        double balance = (valid) ? 20.00 : -1;

        return new AccountRequestModel(firstname, lastname, new BigDecimal(balance));
    }
}
