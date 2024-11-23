package com.ihomziak.transactionmanagementservice.dto;

import com.ihomziak.transactioncommon.utils.TransactionStatus;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TransactionResponseDTO {
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
