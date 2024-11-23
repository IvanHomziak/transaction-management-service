package com.ihomziak.transactionmanagementservice.dto;

import com.ihomziak.transactioncommon.utils.TransactionStatus;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDTO {
    private String senderUuid;
    private String receiverUuid;
    private Double amount;
    private TransactionStatus transactionStatus;
}
