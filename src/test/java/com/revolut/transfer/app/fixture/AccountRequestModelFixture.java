package com.revolut.transfer.app.fixture;

import com.revolut.transfer.app.model.AccountRequestModel;

public class AccountRequestModelFixture {
    public static AccountRequestModel requestModelFixture(boolean valid) {
        String firstname = (valid) ? "Johndoe" : "";
        String lastname = (valid) ? "Revolut" : null;
        float balance = (valid) ? 20.00F : -1;

        return new AccountRequestModel(firstname, lastname, balance);
    }
}
