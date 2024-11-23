package com.ihomziak.transactionmanagementservice.dto;

import com.ihomziak.transactioncommon.utils.TransactionStatus;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TransactionEventResponseDTO {
    private String transactionUuid;
    private TransactionStatus transactionStatus;
    private String statusMessage;
}