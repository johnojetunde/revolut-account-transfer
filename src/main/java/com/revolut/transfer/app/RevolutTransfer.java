package com.revolut.transfer.app;

import com.google.gson.GsonBuilder;
import com.revolut.transfer.app.controller.AccountController;
import com.revolut.transfer.app.exception.api.ApiViolationException;
import com.revolut.transfer.app.exception.api.InternalServerErrorException;
import com.revolut.transfer.app.exception.handler.GeneralExceptionHandler;
import com.revolut.transfer.app.model.AccountRequestModel;
import com.revolut.transfer.app.service.ModelValidator;
import com.revolut.transfer.domain.model.Account;
import com.revolut.transfer.domain.service.DefaultAccountService;
import com.revolut.transfer.persistence.service.ConcurrentHashMapAccountStorage;

import java.util.concurrent.ConcurrentHashMap;

import static com.revolut.transfer.app.factory.ValidatorFactory.getValidator;
import static spark.Spark.*;

public class RevolutTransfer {
    public static void main(String... args) {
        final ConcurrentHashMap<String, Account> database = new ConcurrentHashMap<>();
        var gson = new GsonBuilder().setPrettyPrinting().create();
        var accountStorage = new ConcurrentHashMapAccountStorage(database);
        var accountService = new DefaultAccountService(accountStorage);
        var validator = getValidator();
        var responseModelValidator = new ModelValidator<AccountRequestModel>(validator);
        var accountController = new AccountController(responseModelValidator, accountService, gson);
        var exceptionHandler = new GeneralExceptionHandler(gson);

        get("/accounts", accountController::getAll);
        get("/accounts/:id", accountController::get);
        post("/accounts", accountController::post);
        patch("/accounts/:id", accountController::patch);
        after((request, response) -> response.type("application/json"));

        exception(InternalServerErrorException.class, exceptionHandler::generalAccountException);
        exception(ApiViolationException.class, exceptionHandler::violationException);

        //        patch("/v1/accounts/:id/deposit/:amount/:currency", (request, response) -> "response");
        //        post("/v1/transfers", (request, response) -> "response");
    }
}
