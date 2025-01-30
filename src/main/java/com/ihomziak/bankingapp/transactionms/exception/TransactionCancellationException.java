package com.ihomziak.bankingapp.transactionms.exception;

public class TransactionCancellationException extends RuntimeException {
    public TransactionCancellationException(String message) {
        super(message);
    }
}