package com.ihomziak.transactionmanagementservice.utils;

import com.ihomziak.transactioncommon.utils.TransactionStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionStatusCheckerTest {

    @Test
    void isTransactionStatusNewCompletedOrFailed_ShouldReturnTrue_ForNewStatus() {
        assertTrue(TransactionStatusChecker.isTransactionStatusStartedCompletedCanceled(TransactionStatus.NEW));
    }

    @Test
    void isTransactionStatusNewCompletedOrFailed_ShouldReturnTrue_ForCompletedStatus() {
        assertTrue(TransactionStatusChecker.isTransactionStatusStartedCompletedCanceled(TransactionStatus.COMPLETED));
    }

    @Test
    void isTransactionStatusNewCompletedOrFailed_ShouldReturnTrue_ForFailedStatus() {
        assertTrue(TransactionStatusChecker.isTransactionStatusStartedCompletedCanceled(TransactionStatus.FAILED));
    }

    @Test
    void isTransactionStatusNewCompletedOrFailed_ShouldReturnFalse_ForCreatedStatus() {
        assertFalse(TransactionStatusChecker.isTransactionStatusStartedCompletedCanceled(TransactionStatus.CREATED));
    }

    @Test
    void isTransactionStatusNewCompletedOrFailed_ShouldReturnFalse_ForStartedStatus() {
        assertFalse(TransactionStatusChecker.isTransactionStatusStartedCompletedCanceled(TransactionStatus.STARTED));
    }

    @Test
    void isTransactionStatusNewCompletedOrFailed_ShouldReturnFalse_ForDeclinedStatus() {
        assertFalse(TransactionStatusChecker.isTransactionStatusStartedCompletedCanceled(TransactionStatus.DECLINED));
    }

    @Test
    void isTransactionStatusNewCompletedOrFailed_ShouldReturnFalse_ForCanceledStatus() {
        assertFalse(TransactionStatusChecker.isTransactionStatusStartedCompletedCanceled(TransactionStatus.CANCELED));
    }
}
