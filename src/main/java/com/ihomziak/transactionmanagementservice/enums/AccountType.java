package com.ihomziak.transactionmanagementservice.enums;

public enum AccountType {

    CHECKING("CHECKING"), SAVINGS("SAVINGS"), CREDIT("CREDIT");

    private final String accountType;

    private AccountType(String accountType){
        this.accountType = accountType;
    }

    @Override
    public String toString() {
        return this.accountType;
    }
}
