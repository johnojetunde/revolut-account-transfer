package com.revolut.transfer.app.api;

import com.google.gson.Gson;
import com.revolut.transfer.app.RevolutTransfer;
import com.revolut.transfer.app.model.AccountRequestModel;
import com.revolut.transfer.app.model.AccountResponseModel;
import okhttp3.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.io.IOException;
import java.math.BigDecimal;

import static com.revolut.transfer.app.fixture.AccountRequestModelFixture.requestModelFixture;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AccountAPITests {
    private static String BASE_URL;
    private static OkHttpClient client;
    private static Gson gson;

    @BeforeAll
    public static void startServer() {
        RevolutTransfer.main("");
        int PORT = Spark.port();
        client = new OkHttpClient();
        BASE_URL = "http://localhost:" + PORT;
        gson = new Gson();

    }

    @AfterAll
    public static void stopServer() {
        Spark.stop();
    }

    @Test
    void createNewAccountSuccessfully() throws IOException {
        AccountRequestModel account = requestModelFixture(true);
        String accountJson = gson.toJson(account);
        Response response = createAccount(accountJson);

        assertAll("assert http params",
                () -> assertEquals(200, response.code()),
                () -> assertEquals("application/json", response.header("Content-Type")),
                () -> assertNotNull(response.body())
        );
    }

    @Test
    void createNewAccountFailedWithErrors() throws IOException {
        AccountRequestModel account = requestModelFixture(false);
        String accountJson = gson.toJson(account);
        Response response = createAccount(accountJson);

        assertAll("assert http params",
                () -> assertEquals(400, response.code()),
                () -> assertEquals("application/json", response.header("Content-Type")),
                () -> assertNotNull(response.body())
        );
    }

    private Response createAccount(String accountJson) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/accounts")
                .post(RequestBody.create(MediaType.get("application/json"), accountJson))
                .build();

        Call call = client.newCall(request);
        return call.execute();
    }

    @Test
    void getAccountBydIdSuccessful() throws IOException {
        String accountJson = gson.toJson(requestModelFixture(true));
        Response createResponse = createAccount(accountJson);

        AccountResponseModel responseModel = gson.fromJson(createResponse.body().charStream(), AccountResponseModel.class);

        Request request = new Request.Builder()
                .url(BASE_URL + "/accounts/" + responseModel.getId())
                .get()
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        assertAll("assert http params",
                () -> assertEquals(200, response.code()),
                () -> assertEquals("application/json", response.header("Content-Type")),
                () -> assertNotNull(response.body())
        );
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

        assertAll("assert http params",
                () -> assertEquals(200, response.code()),
                () -> assertEquals("application/json", response.header("Content-Type")),
                () -> assertNotNull(response.body())
        );
    }

    @Test
    void getAccountBydIdFailed() throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/accounts/2012344")
                .get()
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        assertAll("assert http params",
                () -> assertEquals(500, response.code()),
                () -> assertEquals("application/json", response.header("Content-Type")),
                () -> assertNotNull(response.body())
        );
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

        assertAll("assert http params",
                () -> assertEquals(200, response.code()),
                () -> assertEquals("application/json", response.header("Content-Type")),
                () -> assertNotNull(response.body()),
                () -> assertEquals(500.0, updatedModel.getBalance().doubleValue()),
                () -> assertEquals("Ginger", updatedModel.getFirstname())
        );
    }
}
