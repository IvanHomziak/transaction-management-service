package com.ihomziak.transactionmanagementservice.dto;

import com.ihomziak.transactioncommon.AccountType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class AccountResponseDTO {
    private long accountId;
    private AccountHolderDTO accountHolderDTO;
    private String accountNumber;
    private AccountType accountType;
    private double balance;
    private String UUID;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
}
