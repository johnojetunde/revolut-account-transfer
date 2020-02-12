package com.revolut.transfer.app.api;

import com.revolut.transfer.app.model.AccountRequestModel;
import com.revolut.transfer.app.model.AccountResponseModel;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static com.revolut.transfer.app.fixture.AccountRequestModelFixture.requestModelFixture;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTest extends BaseApiTest {

    @Test
    void createNewAccountSuccessfully() throws IOException {
        AccountRequestModel account = requestModelFixture(true);
        String accountJson = gson.toJson(account);
        Response response = createAccount(accountJson);

        assertHttpResponse(response, 200);
    }

    @Test
    void createNewAccountFailedWithErrors() throws IOException {
        AccountRequestModel account = requestModelFixture(false);
        String accountJson = gson.toJson(account);
        Response response = createAccount(accountJson);

        assertHttpResponse(response, 400);
    }

    @Test
    void getAccountBydIdSuccessful() throws IOException {
        String accountJson = gson.toJson(requestModelFixture(true));
        Response createResponse = createAccount(accountJson);

        AccountResponseModel responseModel = gson.fromJson(createResponse.body().charStream(), AccountResponseModel.class);
        Response response = getAccountById(responseModel.getId());

        assertHttpResponse(response, 200);
    }

    @Test
    void getAllAccountsSuccessful() throws IOException {
        String accountJson = gson.toJson(requestModelFixture(true));
        createAccount(accountJson);

        Request request = new Request.Builder()
                .url(BASE_URL + "/accounts")
                .get()
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        assertHttpResponse(response, 200);
    }

    @Test
    void getAccountBydIdFailed() throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/accounts/2012344")
                .get()
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        assertHttpResponse(response, 500);
    }

    @Test
    void updateAccountSuccessfully() throws IOException {
        AccountRequestModel requestModel = requestModelFixture(true);
        String accountJson = gson.toJson(requestModel);
        Response createResponse = createAccount(accountJson);
        AccountResponseModel responseModel = gson.fromJson(createResponse.body().charStream(), AccountResponseModel.class);

        requestModel.setBalance(new BigDecimal("500.0"));
        requestModel.setFirstname("Ginger");

        Request request = new Request.Builder()
                .url(BASE_URL + "/accounts/" + responseModel.getId())
                .patch(RequestBody.create(MediaType.get("application/json"), gson.toJson(requestModel)))
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        AccountResponseModel updatedModel = gson.fromJson(response.body().charStream(), AccountResponseModel.class);

        assertHttpResponse(response, 200);
        assertAll("assert response values",
                () -> assertEquals(500.0, updatedModel.getBalance().doubleValue()),
                () -> assertEquals("Ginger", updatedModel.getFirstname())
        );
    }
}
