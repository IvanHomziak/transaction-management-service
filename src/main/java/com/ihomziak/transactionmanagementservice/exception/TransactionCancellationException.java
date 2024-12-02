package com.ihomziak.transactionmanagementservice.exception;

public class TransactionCancellationException extends RuntimeException {
    public TransactionCancellationException(String message) {
        super(message);
    }
}