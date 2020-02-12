package com.revolut.transfer.app.api;

import com.revolut.transfer.app.model.AccountRequestModel;
import com.revolut.transfer.app.model.AccountResponseModel;
import com.revolut.transfer.app.model.TransferRequestModel;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferTest extends BaseApiTest {

    @Test
    void transferSuccessfully() throws IOException {
        AccountRequestModel account1 = createRequest("JohnDoe", 100.00);
        AccountRequestModel account2 = createRequest("Smith", 10.00);
        String accountJson1 = gson.toJson(account1);
        String accountJson2 = gson.toJson(account2);

        AccountResponseModel responseModel1 = toResponseModel(createAccount(accountJson1));
        AccountResponseModel responseModel2 = toResponseModel(createAccount(accountJson2));
        TransferRequestModel transferRequestModel = new TransferRequestModel(
                new BigDecimal("20.2"),
                responseModel1.getId(), responseModel2.getId()
        );

        String transferRequestJson = gson.toJson(transferRequestModel);

        Request request = new Request.Builder()
                .url(BASE_URL + "/transfers")
                .post(RequestBody.create(MediaType.get("application/json"), transferRequestJson))
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        assertHttpResponse(response, 200);

        AccountResponseModel responseModel1AfterTransfer = toResponseModel(getAccountById(responseModel1.getId()));
        AccountResponseModel responseModel2AfterTransfer = toResponseModel(getAccountById(responseModel2.getId()));

        assertAll("assert all balances",
                () -> assertEquals(79.8, responseModel1AfterTransfer.getBalance().doubleValue()),
                () -> assertEquals(30.2, responseModel2AfterTransfer.getBalance().doubleValue()));
    }

    private AccountResponseModel toResponseModel(Response response) {
        return gson.fromJson(response.body().charStream(), AccountResponseModel.class);
    }

    private AccountRequestModel createRequest(String firstname, Double balance) {
        return new AccountRequestModel(firstname, "Revolut", new BigDecimal(balance));
    }
}
