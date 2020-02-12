package com.revolut.transfer.app;

import com.google.gson.GsonBuilder;
import com.revolut.transfer.app.controller.AccountController;
import com.revolut.transfer.app.controller.TransferController;
import com.revolut.transfer.app.exception.api.ApiViolationException;
import com.revolut.transfer.app.exception.api.InternalServerErrorException;
import com.revolut.transfer.app.exception.handler.GeneralExceptionHandler;
import com.revolut.transfer.app.model.AccountRequestModel;
import com.revolut.transfer.app.model.TransferRequestModel;
import com.revolut.transfer.app.service.ModelValidator;
import com.revolut.transfer.domain.model.Account;
import com.revolut.transfer.domain.model.Transfer;
import com.revolut.transfer.domain.service.*;
import com.revolut.transfer.persistence.service.ConcurrentHashMapAccountStorage;
import com.revolut.transfer.persistence.service.ConcurrentHashMapTransferStorage;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.revolut.transfer.app.factory.ValidatorFactory.getValidator;
import static java.util.Collections.singletonList;
import static spark.Spark.*;

public class RevolutTransfer {
    public static void main(String... args) {
        final ConcurrentHashMap<String, Account> accountDatabase = new ConcurrentHashMap<>();
        final ConcurrentHashMap<String, Transfer> transferDatabase = new ConcurrentHashMap<>();

        var gson = new GsonBuilder().setPrettyPrinting().create();
        var accountStorage = new ConcurrentHashMapAccountStorage(accountDatabase);
        var accountService = new DefaultAccountService(accountStorage);
        var validator = getValidator();
        var responseModelValidator = new ModelValidator<AccountRequestModel>(validator);
        var transferModelValidator = new ModelValidator<TransferRequestModel>(validator);
        var transferStorage = new ConcurrentHashMapTransferStorage(transferDatabase);
        List<TransferValidator> transferValidators = singletonList(new BalanceTransferValidator());
        var transferService = new DefaultTransferService(transferStorage, accountStorage, transferValidators);

        var accountController = new AccountController(responseModelValidator, accountService, gson);
        var transferController = new TransferController(transferModelValidator, transferService, gson);

        var exceptionHandler = new GeneralExceptionHandler(gson);

        get("/accounts", accountController::getAll);
        get("/accounts/:id", accountController::get);
        post("/accounts", accountController::post);
        patch("/accounts/:id", accountController::patch);

        get("/transfers", transferController::getAll);
        get("/transfers/:id", transferController::get);
        post("/transfers", transferController::post);

        after((request, response) -> response.type("application/json"));

        exception(InternalServerErrorException.class, exceptionHandler::generalAccountException);
        exception(ApiViolationException.class, exceptionHandler::violationException);
    }
}
