package com.ihomziak.bankingapp.transactionms.dto;

import com.ihomziak.bankingapp.common.utils.TransactionStatus;
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