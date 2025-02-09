package com.ihomziak.bankingapp.transactionms.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TransactionStatusResponseDTO {
    private String transactionUuid;
    private LocalDateTime startTransactionTime;
}