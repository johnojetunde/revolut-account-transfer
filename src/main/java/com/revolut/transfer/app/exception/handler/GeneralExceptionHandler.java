package com.revolut.transfer.app.exception.handler;

import com.google.gson.Gson;
import com.revolut.transfer.app.exception.api.ApiViolationException;
import com.revolut.transfer.app.exception.api.InternalServerErrorException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class GeneralExceptionHandler {
    private Gson gson;

    public void generalAccountException(InternalServerErrorException exception, Request request, Response response) {
        log.info("handling error with status 500", exception);
        response.status(500);
        response.type("application/json");
        response.body("{\"error\":\"" + exception.getMessage() + "\"}");
    }

    public void violationException(ApiViolationException exception, Request request, Response response) {
        log.info("handling error with status 400", exception);
        Map<String, List<String>> violations = exception.getViolations();

        response.status(400);
        response.type("application/json");
        response.body(gson.toJson(violations));
    }
}
