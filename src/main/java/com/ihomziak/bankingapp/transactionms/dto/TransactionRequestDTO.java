package com.ihomziak.bankingapp.transactionms.dto;

import com.ihomziak.bankingapp.common.utils.TransactionStatus;
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
