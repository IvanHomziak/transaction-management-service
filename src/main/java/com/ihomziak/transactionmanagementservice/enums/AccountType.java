package com.ihomziak.transactionmanagementservice.enums;

public enum AccountType {

    CHECKING("Checking"), SAVINGS("Savings"), CREDIT("Credit");

    private final String accountType;

    private AccountType(String accountType){
        this.accountType = accountType;
    }

    @Override
    public String toString() {
        return this.accountType;
    }
}
