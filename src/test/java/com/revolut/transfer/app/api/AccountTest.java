package com.revolut.transfer.app.api;

import com.revolut.transfer.app.model.AccountResponseModel;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static com.revolut.transfer.app.fixture.AccountRequestModelFixture.requestModelFixture;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTest extends BaseApiTest<AccountResponseModel> {

    @Test
    void createNewAccountSuccessfully() throws IOException {
        var account = requestModelFixture(true);
        var accountJson = gson.toJson(account);
        var response = createAccount(accountJson);

        assertHttpResponse(response, 200);
    }

    @Test
    void createNewAccountFailedWithErrors() throws IOException {
        var account = requestModelFixture(false);
        var accountJson = gson.toJson(account);
        var response = createAccount(accountJson);

        assertHttpResponse(response, 400);
    }

    @Test
    void getAccountBydIdSuccessful() throws IOException {
        var accountJson = gson.toJson(requestModelFixture(true));
        var accountCreationResponse = createAccount(accountJson);

        var responseModel = convertToModel(accountCreationResponse);
        var response = httpGet("/accounts/" + responseModel.getId());

        assertHttpResponse(response, 200);
    }

    @Test
    void getAllAccountsSuccessful() throws IOException {
        var accountJson = gson.toJson(requestModelFixture(true));
        createAccount(accountJson);

        var response = httpGet("/accounts");

        assertListOfObject(response);
    }

    @Test
    void getAccountBydIdFailed() throws IOException {
        var response = httpGet("/accounts/2012344");

        assertHttpResponse(response, 500);
    }

    @Test
    void updateAccountSuccessfully() throws IOException {
        var requestModel = requestModelFixture(true);
        var accountJson = gson.toJson(requestModel);
        var accountCreationResponse = createAccount(accountJson);
        var responseModel = convertToModel(accountCreationResponse);
        requestModel.setBalance(new BigDecimal("500.0"));
        requestModel.setFirstname("Ginger");

        var response = httpPatch("/accounts/" + responseModel.getId(), gson.toJson(requestModel));
        var updatedModel = convertToModel(response);

        assertHttpResponse(response, 200);

        assertAll("assert response values",
                () -> assertEquals(500.0, updatedModel.getBalance().doubleValue()),
                () -> assertEquals("Ginger", updatedModel.getFirstname())
        );
    }
}
