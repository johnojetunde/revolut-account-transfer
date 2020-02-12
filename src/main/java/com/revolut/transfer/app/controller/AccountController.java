package com.revolut.transfer.app.controller;

import com.google.gson.Gson;
import com.revolut.transfer.app.exception.ViolationException;
import com.revolut.transfer.app.exception.api.ApiViolationException;
import com.revolut.transfer.app.exception.api.InternalServerErrorException;
import com.revolut.transfer.app.model.AccountRequestModel;
import com.revolut.transfer.app.model.AccountResponseModel;
import com.revolut.transfer.app.service.ModelValidator;
import com.revolut.transfer.domain.exception.AccountServiceException;
import com.revolut.transfer.domain.service.AccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.revolut.transfer.app.model.AccountResponseModel.fromAccount;

@Slf4j
@AllArgsConstructor
public class AccountController {
    private final ModelValidator<AccountRequestModel> validator;
    private AccountService accountService;
    private Gson gson;

    public Object getAll(Request request, Response response) {
        try {
            CompletableFuture<Object> responseBody = accountService.getAll()
                    .thenApply(s -> {
                        List<AccountResponseModel> responseModels = s.stream()
                                .map(AccountResponseModel::fromAccount)
                                .collect(Collectors.toList());

                        return gson.toJson(responseModels);
                    });

            setSuccessStatus(responseBody, response);

            return responseBody.get();
        } catch (AccountServiceException e) {
            throw new InternalServerErrorException(e.getMessage());
        } catch (Exception e) {
            throw new InternalServerErrorException("Error fetching account details");
        }
    }

    public Object get(Request request, Response response) {
        try {
            String id = request.params("id");
            CompletableFuture<Object> responseBody = accountService.get(id)
                    .thenApply(s -> gson.toJson(fromAccount(s)));

            setSuccessStatus(responseBody, response);

            return responseBody.get();
        } catch (AccountServiceException e) {
            throw new InternalServerErrorException(e.getMessage());
        } catch (Exception e) {
            throw new InternalServerErrorException("Error fetching account details");
        }
    }

    public Object post(Request request, Response response) {
        try {
            AccountRequestModel requestModel = new Gson().fromJson(request.body(), AccountRequestModel.class);

            validator.validate(requestModel);

            CompletableFuture<Object> responseBody = accountService.create(
                    requestModel.getFirstname(),
                    requestModel.getLastname(),
                    requestModel.getBalance()
            ).thenApply(a -> gson.toJson(fromAccount(a)));

            setSuccessStatus(responseBody, response);

            return responseBody.get();
        } catch (ViolationException e) {
            throw new ApiViolationException(e);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error creating account");
        }
    }

    public Object patch(Request request, Response response) {
        try {
            String id = request.params("id");
            AccountRequestModel requestModel = new Gson().fromJson(request.body(), AccountRequestModel.class);

            CompletableFuture<Object> responseBody = accountService.update(id, requestModel.toAccount()
            ).thenApply(a -> gson.toJson(fromAccount(a)));

            setSuccessStatus(responseBody, response);

            return responseBody.get();
        } catch (Exception e) {
            throw new InternalServerErrorException("Error updating account");
        }
    }

    private void setSuccessStatus(CompletableFuture<Object> responseBody, Response response) {
        responseBody.thenAccept(r -> response.status(200));
    }
}
