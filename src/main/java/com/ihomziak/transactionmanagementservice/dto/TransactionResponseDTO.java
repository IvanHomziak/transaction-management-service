package com.ihomziak.transactionmanagementservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionResponseDTO {

    private String senderUuid;
    private Double senderBalance;
    private String recipientUuid;
    private Double recipientBalance;


    /**
     * String transactionUuid;
     * TimeDate startTransactionTime;
     * TimeDate finishedTransactionTime;
     * Enum
     */
}
