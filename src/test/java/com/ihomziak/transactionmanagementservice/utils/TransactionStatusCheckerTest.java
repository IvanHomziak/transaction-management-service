package com.ihomziak.transactionmanagementservice.utils;

import com.ihomziak.transactioncommon.utils.TransactionStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionStatusCheckerTest {

    @Test
    void isTransactionStatusNewCompletedOrFailed_ShouldReturnTrue_ForNewStatus() {
        assertTrue(TransactionStatusChecker.isTransactionStatusCompleted(TransactionStatus.NEW));
    }

    @Test
    void isTransactionStatusNewCompletedOrFailed_ShouldReturnTrue_ForCompletedStatus() {
        assertTrue(TransactionStatusChecker.isTransactionStatusCompleted(TransactionStatus.COMPLETED));
    }

    @Test
    void isTransactionStatusNewCompletedOrFailed_ShouldReturnTrue_ForFailedStatus() {
        assertTrue(TransactionStatusChecker.isTransactionStatusCompleted(TransactionStatus.FAILED));
    }

    @Test
    void isTransactionStatusNewCompletedOrFailed_ShouldReturnFalse_ForCreatedStatus() {
        assertFalse(TransactionStatusChecker.isTransactionStatusCompleted(TransactionStatus.CREATED));
    }

    @Test
    void isTransactionStatusNewCompletedOrFailed_ShouldReturnFalse_ForStartedStatus() {
        assertFalse(TransactionStatusChecker.isTransactionStatusCompleted(TransactionStatus.STARTED));
    }

    @Test
    void isTransactionStatusNewCompletedOrFailed_ShouldReturnFalse_ForDeclinedStatus() {
        assertFalse(TransactionStatusChecker.isTransactionStatusCompleted(TransactionStatus.DECLINED));
    }

    @Test
    void isTransactionStatusNewCompletedOrFailed_ShouldReturnFalse_ForCanceledStatus() {
        assertFalse(TransactionStatusChecker.isTransactionStatusCompleted(TransactionStatus.CANCELED));
    }
}
