package com.ihomziak.transactionmanagementservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionResponseDTO {

    private String senderUuid;
    private double senderBalance;
    private String recipientUuid;
    private double recipientBalance;
}
