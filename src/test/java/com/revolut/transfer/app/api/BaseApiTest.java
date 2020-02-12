package com.revolut.transfer.app.api;

import com.google.gson.Gson;
import com.revolut.transfer.app.RevolutTransfer;
import okhttp3.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import spark.Spark;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BaseApiTest {
    static String BASE_URL;
    static OkHttpClient client;
    static Gson gson;

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

    Response createAccount(String accountJson) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/accounts")
                .post(RequestBody.create(MediaType.get("application/json"), accountJson))
                .build();

        Call call = client.newCall(request);
        return call.execute();
    }

    Response getAccountById(String id) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/accounts/" + id)
                .get()
                .build();

        Call call = client.newCall(request);
        return call.execute();
    }

    void assertHttpResponse(Response response, int statusCode) {
        assertAll("assert http params",
                () -> assertEquals(statusCode, response.code()),
                () -> assertEquals("application/json", response.header("Content-Type")),
                () -> assertNotNull(response.body())
        );
    }
}
