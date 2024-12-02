package com.ihomziak.transactionmanagementservice.utils;

import com.ihomziak.transactioncommon.utils.TransactionStatus;

public class TransactionStatusChecker {

    private TransactionStatusChecker() {
    }

    public static boolean isTransactionStatusCompleted(TransactionStatus transaction) {
        return transaction.equals(TransactionStatus.COMPLETED);

    }
}