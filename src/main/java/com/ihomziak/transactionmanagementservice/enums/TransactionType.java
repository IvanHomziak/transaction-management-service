package com.ihomziak.transactionmanagementservice.enums;

public enum TransactionType {

    TRANSFER("Transfer");

    private final String name;

    private TransactionType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
