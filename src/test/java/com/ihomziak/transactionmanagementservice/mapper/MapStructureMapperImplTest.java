package com.ihomziak.transactionmanagementservice.mapper.impl;

import com.ihomziak.transactionmanagementservice.dto.*;
import com.ihomziak.transactionmanagementservice.entity.Transaction;
import com.ihomziak.transactioncommon.utils.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MapStructureMapperImplTest {

    private MapStructureMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new MapStructureMapperImpl();
    }

    @Test
    void mapTransactionRequestDTOToTransaction_NullInput() {
        assertNull(mapper.mapTransactionRequestDTOToTransaction(null));
    }

    @Test
    void mapTransactionRequestDTOToTransaction_ValidInput() {
        TransactionRequestDTO requestDTO = new TransactionRequestDTO();
        requestDTO.setSenderUuid("sender-uuid");
        requestDTO.setReceiverUuid("receiver-uuid");
        requestDTO.setAmount(100.0);
        requestDTO.setTransactionStatus(TransactionStatus.NEW);

        Transaction transaction = mapper.mapTransactionRequestDTOToTransaction(requestDTO);

        assertNotNull(transaction);
        assertEquals("sender-uuid", transaction.getSenderUuid());
        assertEquals("receiver-uuid", transaction.getReceiverUuid());
        assertEquals(100.0, transaction.getAmount());
        assertEquals(TransactionStatus.NEW, transaction.getTransactionStatus());
    }

    @Test
    void mapTransactionToTransactionResponseDTO_NullInput() {
        assertNull(mapper.mapTransactionToTransactionResponseDTO(null));
    }

    @Test
    void mapTransactionToTransactionResponseDTO_ValidInput() {
        Transaction transaction = new Transaction();
        transaction.setTransactionUuid("transaction-uuid");
        transaction.setTransactionStatus(TransactionStatus.COMPLETED);

        TransactionResponseDTO responseDTO = mapper.mapTransactionToTransactionResponseDTO(transaction);

        assertNotNull(responseDTO);
        assertEquals("transaction-uuid", responseDTO.getTransactionUuid());
        assertEquals(TransactionStatus.COMPLETED, responseDTO.getTransactionStatus());
    }

    @Test
    void mapTransactionToTransactionStatusResponseDTO_NullInput() {
        assertNull(mapper.mapTransactionToTransactionStatusResponseDTO(null));
    }

    @Test
    void mapTransactionToTransactionStatusResponseDTO_ValidInput() {
        Transaction transaction = new Transaction();
        transaction.setTransactionUuid("transaction-uuid");
        transaction.setTransactionDate(LocalDateTime.of(2024, 1, 1, 12, 0));

        TransactionStatusResponseDTO responseDTO = mapper.mapTransactionToTransactionStatusResponseDTO(transaction);

        assertNotNull(responseDTO);
        assertEquals("transaction-uuid", responseDTO.getTransactionUuid());
        assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0), responseDTO.getStartTransactionTime());
    }

    @Test
    void mapTransactionToTransactionEventRequestDTO_NullInput() {
        assertNull(mapper.mapTransactionToTransactionEventRequestDTO(null));
    }

    @Test
    void mapTransactionToTransactionEventRequestDTO_ValidInput() {
        Transaction transaction = new Transaction();
        transaction.setSenderUuid("sender-uuid");
        transaction.setReceiverUuid("receiver-uuid");
        transaction.setAmount(200.0);
        transaction.setTransactionStatus(TransactionStatus.COMPLETED);
        transaction.setTransactionUuid("transaction-uuid");

        TransactionEventRequestDTO eventRequestDTO = mapper.mapTransactionToTransactionEventRequestDTO(transaction);

        assertNotNull(eventRequestDTO);
        assertEquals("sender-uuid", eventRequestDTO.getSenderUuid());
        assertEquals("receiver-uuid", eventRequestDTO.getReceiverUuid());
        assertEquals(200.0, eventRequestDTO.getAmount());
        assertEquals(TransactionStatus.COMPLETED, eventRequestDTO.getTransactionStatus());
        assertEquals("transaction-uuid", eventRequestDTO.getTransactionUuid());
    }
}
