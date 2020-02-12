package com.revolut.transfer.app.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.revolut.transfer.app.RevolutTransfer;
import com.revolut.transfer.app.model.AccountResponseModel;
import okhttp3.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import spark.Spark;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BaseApiTest<T> {
    static String BASE_URL;
    static OkHttpClient client;
    static Gson gson;

    @BeforeAll
    public static void startServer() {
        RevolutTransfer.main("");
        var PORT = Spark.port();
        client = new OkHttpClient();
        BASE_URL = "http://localhost:" + PORT;
        gson = new Gson();

    }

    @AfterAll
    public static void stopServer() {
        Spark.stop();
    }

    Response createAccount(String accountJson) throws IOException {
        return httpPost("/accounts", accountJson);
    }

    Response httpPost(String path, String body) throws IOException {
        var request = new Request.Builder()
                .url(BASE_URL + path)
                .post(RequestBody.create(MediaType.get("application/json"), body))
                .build();

        return fireRequest(request);
    }

    Response httpPatch(String path, String body) throws IOException {
        var request = new Request.Builder()
                .url(BASE_URL + path)
                .patch(RequestBody.create(MediaType.get("application/json"), body))
                .build();

        return fireRequest(request);
    }

    Response httpGet(String path) throws IOException {
        var request = new Request.Builder()
                .url(BASE_URL + path)
                .get()
                .build();

        return fireRequest(request);
    }

    private Response fireRequest(Request request) throws IOException {
        return client.newCall(request).execute();
    }

    void assertHttpResponse(Response response, int statusCode) {
        assertAll("assert http params",
                () -> assertEquals(statusCode, response.code()),
                () -> assertEquals("application/json", response.header("Content-Type")),
                () -> assertNotNull(response.body())
        );
    }

    void assertListOfObject(Response response) {
        Type type = new TypeToken<ArrayList<T>>() {}.getType();

        List<T> responseObjects = gson.fromJson(response.body().charStream(), type);

        assertHttpResponse(response, 200);
        assertFalse(responseObjects.isEmpty());
    }

    AccountResponseModel convertToAccountModel(Response response) {
        return gson.fromJson(response.body().charStream(), AccountResponseModel.class);
    }

    Map convertToMap(Response response) {
        return gson.fromJson(response.body().charStream(), Map.class);
    }
}
