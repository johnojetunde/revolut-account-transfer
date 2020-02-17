package com.revolut.transfer.app.controller;

import com.google.gson.Gson;
import com.revolut.transfer.app.exception.RequestValidationException;
import com.revolut.transfer.app.exception.ViolationException;
import com.revolut.transfer.app.exception.api.ApiViolationException;
import com.revolut.transfer.app.exception.api.BadRequestException;
import com.revolut.transfer.app.exception.api.InternalServerErrorException;
import com.revolut.transfer.app.model.TransferRequestModel;
import com.revolut.transfer.app.model.TransferResponseModel;
import com.revolut.transfer.app.service.ModelValidator;
import com.revolut.transfer.domain.exception.TransferServiceException;
import com.revolut.transfer.domain.service.TransferService;
import lombok.AllArgsConstructor;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.revolut.transfer.app.model.TransferResponseModel.fromModel;

@AllArgsConstructor
public class TransferController {
    private final ModelValidator<TransferRequestModel> validator;
    private TransferService transferService;
    private Gson gson;

    public Object getAll(Request request, Response response) {
        try {
            CompletableFuture<Object> responseBody = transferService.getAll()
                    .thenApply(s -> {
                        List<TransferResponseModel> responseModels = s.stream()
                                .map(TransferResponseModel::fromModel)
                                .collect(Collectors.toList());

                        return gson.toJson(responseModels);
                    });

            setSuccessStatus(responseBody, response);

            return responseBody.get();
        } catch (TransferServiceException e) {
            throw new InternalServerErrorException(e.getMessage());
        } catch (Exception e) {
            throw new InternalServerErrorException("Error retrieving all transfers");
        }
    }

    public Object get(Request request, Response response) {
        try {
            String id = request.params("id");
            CompletableFuture<Object> responseBody = transferService.get(id)
                    .thenApply(s -> gson.toJson(fromModel(s)));

            setSuccessStatus(responseBody, response);

            return responseBody.get();
        } catch (TransferServiceException e) {
            throw new InternalServerErrorException(e.getMessage());
        } catch (Exception e) {
            throw new InternalServerErrorException("Error fetching transfer log details");
        }
    }

    public Object post(Request request, Response response) {
        try {
            TransferRequestModel requestModel = gson.fromJson(request.body(), TransferRequestModel.class);

            validator.validate(requestModel);

            ensureDifferentSenderAndReceiverAccount(requestModel);

            CompletableFuture<Object> responseBody = transferService.transfer(
                    requestModel.toModel()
            ).thenApply(a -> gson.toJson(fromModel(a)));

            setSuccessStatus(responseBody, response);

            return responseBody.get();
        } catch (ViolationException e) {
            throw new ApiViolationException(e);
        } catch (RequestValidationException e) {
            throw new BadRequestException(e.getMessage());
        } catch (TransferServiceException e) {
            throw new InternalServerErrorException(e.getMessage());
        } catch (Exception e) {
            throw new InternalServerErrorException("Unable to complete transfer");
        }
    }

    private void setSuccessStatus(CompletableFuture<Object> responseBody, Response response) {
        responseBody.thenAccept(r -> response.status(200));
    }

    private void ensureDifferentSenderAndReceiverAccount(TransferRequestModel request) throws RequestValidationException {
        if (request.getReceiverAccountId().equals(request.getSenderAccountId())) {
            throw new RequestValidationException("Sender and Receiver accounts must not be the same");
        }
    }
}
