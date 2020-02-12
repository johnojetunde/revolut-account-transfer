package com.revolut.transfer.app.api;

import com.revolut.transfer.app.model.AccountRequestModel;
import com.revolut.transfer.app.model.TransferRequestModel;
import com.revolut.transfer.app.model.TransferResponseModel;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferTest extends BaseApiTest<TransferResponseModel> {

    @Test
    void transferSuccessfully() throws IOException {
        var response = transferMoney();

        assertHttpResponse(response, 200);
    }

    private Response transferMoney() throws IOException {
        var account1 = createRequest("JohnDoe", 100.00);
        var account2 = createRequest("Smith", 10.00);
        var accountJson1 = gson.toJson(account1);
        var accountJson2 = gson.toJson(account2);

        var responseModel1 = convertToAccountModel(createAccount(accountJson1));
        var responseModel2 = convertToAccountModel(createAccount(accountJson2));
        var transferRequestModel = new TransferRequestModel(
                new BigDecimal("20.2"),
                responseModel1.getId(),
                responseModel2.getId()
        );

        var transferRequestJson = gson.toJson(transferRequestModel);
        var response = httpPost("/transfers", transferRequestJson);

        var responseModel1AfterTransfer = convertToAccountModel(
                httpGet("/accounts/" + responseModel1.getId())
        );
        var responseModel2AfterTransfer = convertToAccountModel(
                httpGet("/accounts/" + responseModel2.getId())
        );

        assertAll("assert all balances",
                () -> assertEquals(79.8, responseModel1AfterTransfer.getBalance().doubleValue()),
                () -> assertEquals(30.2, responseModel2AfterTransfer.getBalance().doubleValue()));

        return response;
    }

    @Test
    void transferFailsWithErrors() throws IOException {
        var transferRequestModel = new TransferRequestModel(new BigDecimal("0"), "", "");

        var transferRequestJson = gson.toJson(transferRequestModel);
        var response = httpPost("/transfers", transferRequestJson);
        var errors = convertToMap(response);

        assertHttpResponse(response, 400);
        assertEquals(3, errors.entrySet().size());
    }

    @Test
    void getTransferById() throws IOException {
        var transferResponse = transferMoney();
        var responseModel = convertToTransferModel(transferResponse);

        var response = httpGet("/transfers/" + responseModel.getId());
        var responseFromGet = convertToTransferModel(response);

        assertHttpResponse(response, 200);
        assertEquals(responseModel.getId(), responseFromGet.getId());
        assertEquals(responseModel.getSenderAccountId(), responseFromGet.getSenderAccountId());
    }

    @Test
    void getTransferByIdFailed() throws IOException {
        var response = httpGet("/transfers/2012344");

        assertHttpResponse(response, 500);
    }

    @Test
    void getAllTransfers() throws IOException {
        transferMoney();
        transferMoney();

        assertListOfObject(httpGet("/transfers"));
    }

    private TransferResponseModel convertToTransferModel(Response response) {
        return gson.fromJson(response.body().charStream(), TransferResponseModel.class);
    }

    private AccountRequestModel createRequest(String firstname, Double balance) {
        return new AccountRequestModel(firstname, "Revolut", new BigDecimal(balance));
    }
}
