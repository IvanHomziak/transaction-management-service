package com.ihomziak.transactionmanagementservice.dto;

import com.ihomziak.transactionmanagementservice.utils.AccountType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AccountRequestDTO {

    private String accountNumber;
    private AccountType accountType;
    private double balance;
    private String clientUUID;
}
