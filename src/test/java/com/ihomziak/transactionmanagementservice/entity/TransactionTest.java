package com.ihomziak.transactionmanagementservice.entity;

import com.ihomziak.transactioncommon.utils.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    private Transaction transaction1;
    private Transaction transaction2;

    @BeforeEach
    void setUp() {
        transaction1 = new Transaction();
        transaction1.setTransactionId(1L);
        transaction1.setTransactionUuid("uuid-123");
        transaction1.setSenderUuid("sender-uuid");
        transaction1.setReceiverUuid("receiver-uuid");
        transaction1.setAmount(100.0);
        transaction1.setTransactionStatus(TransactionStatus.NEW);
        transaction1.setTransactionDate(LocalDateTime.now());
        transaction1.setLastUpdate(LocalDateTime.now());

        transaction2 = new Transaction();
        transaction2.setTransactionId(1L);
        transaction2.setTransactionUuid("uuid-123");
        transaction2.setSenderUuid("sender-uuid");
        transaction2.setReceiverUuid("receiver-uuid");
        transaction2.setAmount(100.0);
        transaction2.setTransactionStatus(TransactionStatus.NEW);
        transaction2.setTransactionDate(transaction1.getTransactionDate());
        transaction2.setLastUpdate(transaction1.getLastUpdate());
    }

    @Test
    void testEquals_SameValues_ShouldReturnTrue() {
        assertEquals(transaction1, transaction2);
    }

    @Test
    void testEquals_DifferentValues_ShouldReturnFalse() {
        transaction2.setTransactionUuid("different-uuid");
        assertNotEquals(transaction1, transaction2);
    }

    @Test
    void testHashCode_SameValues_ShouldReturnSameHashCode() {
        assertEquals(transaction1.hashCode(), transaction2.hashCode());
    }

    @Test
    void testHashCode_DifferentValues_ShouldReturnDifferentHashCode() {
        transaction2.setTransactionId(2L);
        assertNotEquals(transaction1.hashCode(), transaction2.hashCode());
    }

    @Test
    void testToString() {
        String expected = "Transaction{transactionId=" + transaction1.getTransactionId() +
                ", transactionUuid='uuid-123', senderUuid='sender-uuid', receiverUuid='receiver-uuid', amount=100.0, " +
                "transactionStatus=NEW, transactionDate=" + transaction1.getTransactionDate() +
                ", lastUpdate=" + transaction1.getLastUpdate() + "}";
        assertEquals(expected, transaction1.toString());
    }
}
