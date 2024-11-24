package com.ihomziak.transactionmanagementservice.utils;

import com.ihomziak.transactioncommon.utils.TransactionStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionStatusCheckerTest {

    @Test
    void isTransactionStatusNewCompletedOrFailed_ShouldReturnTrue_ForNewStatus() {
        assertTrue(TransactionStatusChecker.isTransactionStatusNewCompletedOrFailed(TransactionStatus.NEW));
    }

    @Test
    void isTransactionStatusNewCompletedOrFailed_ShouldReturnTrue_ForCompletedStatus() {
        assertTrue(TransactionStatusChecker.isTransactionStatusNewCompletedOrFailed(TransactionStatus.COMPLETED));
    }

    @Test
    void isTransactionStatusNewCompletedOrFailed_ShouldReturnTrue_ForFailedStatus() {
        assertTrue(TransactionStatusChecker.isTransactionStatusNewCompletedOrFailed(TransactionStatus.FAILED));
    }

    @Test
    void isTransactionStatusNewCompletedOrFailed_ShouldReturnFalse_ForCreatedStatus() {
        assertFalse(TransactionStatusChecker.isTransactionStatusNewCompletedOrFailed(TransactionStatus.CREATED));
    }

    @Test
    void isTransactionStatusNewCompletedOrFailed_ShouldReturnFalse_ForStartedStatus() {
        assertFalse(TransactionStatusChecker.isTransactionStatusNewCompletedOrFailed(TransactionStatus.STARTED));
    }

    @Test
    void isTransactionStatusNewCompletedOrFailed_ShouldReturnFalse_ForDeclinedStatus() {
        assertFalse(TransactionStatusChecker.isTransactionStatusNewCompletedOrFailed(TransactionStatus.DECLINED));
    }

    @Test
    void isTransactionStatusNewCompletedOrFailed_ShouldReturnFalse_ForCanceledStatus() {
        assertFalse(TransactionStatusChecker.isTransactionStatusNewCompletedOrFailed(TransactionStatus.CANCELED));
    }
}
