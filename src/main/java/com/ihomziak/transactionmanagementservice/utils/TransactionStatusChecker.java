package com.ihomziak.transactionmanagementservice.utils;

import com.ihomziak.transactioncommon.utils.TransactionStatus;

public class TransactionStatusChecker {

    private TransactionStatusChecker() {
    }

    public static boolean isTransactionStatusStartedCompletedCanceled(TransactionStatus transaction) {
        return transaction.equals(TransactionStatus.STARTED) ||
                transaction.equals(TransactionStatus.COMPLETED) ||
                transaction.equals(TransactionStatus.CANCELED);
    }
}