package com.revolut.transfer.app.model;

import com.revolut.transfer.domain.model.Transfer;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransferRequestModel {
    @Min(value = 1, message = "Minimum transfer amount is 1")
    private BigDecimal amount;
    @NotBlank(message = "Sender's account id is required")
    private String senderAccountId;
    @NotBlank(message = "Receiver's account id is required")
    private String receiverAccountId;

    public Transfer toModel() {
        return Transfer.builder()
                .amount(this.amount)
                .senderAccountId(this.senderAccountId)
                .receiverAccountId(this.receiverAccountId)
                .build();
    }
}
