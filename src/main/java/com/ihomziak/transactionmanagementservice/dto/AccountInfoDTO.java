package com.ihomziak.transactionmanagementservice.dto;

import com.ihomziak.transactionmanagementservice.enums.AccountType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AccountInfoDTO {

    private String accountNumber;
    private AccountType accountType;
    private double balance;
    private String UUID;
}
