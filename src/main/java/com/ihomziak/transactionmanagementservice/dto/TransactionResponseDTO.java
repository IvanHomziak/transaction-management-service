package com.ihomziak.transactionmanagementservice.dto;

import com.ihomziak.transactionmanagementservice.enums.TransactionStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionResponseDTO {

    private Integer transactionEventId;
    private String transactionUuid;
    private TransactionStatus transactionStatus;
    private String errorMessage;
    private Double updatedSenderBalance;
    private Double updatedReceiverBalance;



    /**
     * String transactionUuid;
     * TimeDate startTransactionTime;
     * TimeDate finishedTransactionTime;
     * Enum
     */
}
