package com.ihomziak.transactionmanagementservice.utils;

import com.ihomziak.transactioncommon.utils.TransactionStatus;

public class TransactionStatusChecker {

    private TransactionStatusChecker() {
    }

    public static boolean isTransactionStatusNewCompletedOrFailed(TransactionStatus transaction) {
        return transaction.equals(TransactionStatus.NEW) ||
                transaction.equals(TransactionStatus.COMPLETED) ||
                transaction.equals(TransactionStatus.FAILED);
    }
}