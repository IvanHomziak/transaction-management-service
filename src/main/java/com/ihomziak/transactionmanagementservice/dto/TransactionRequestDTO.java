package com.ihomziak.transactionmanagementservice.dto;

import com.ihomziak.transactionmanagementservice.utils.TransactionStatus;
import com.ihomziak.transactionmanagementservice.utils.TransactionType;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDTO {

    private Integer transactionEventId;
    private String transactionUuid;
    private String senderUuid;
    private String receiverUuid;
    private Double amount;
    private TransactionStatus transactionStatus;
    private TransactionType transactionType;
}
