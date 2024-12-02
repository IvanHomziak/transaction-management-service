package com.ihomziak.transactionmanagementservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihomziak.transactionmanagementservice.dto.TransactionRequestDTO;
import com.ihomziak.transactionmanagementservice.dto.TransactionStatusResponseDTO;
import com.ihomziak.transactionmanagementservice.entity.Transaction;
import com.ihomziak.transactionmanagementservice.exception.TransactionNotFoundException;
import com.ihomziak.transactionmanagementservice.exceptionhandler.GlobalExceptionHandler;
import com.ihomziak.transactionmanagementservice.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionServiceImpl transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private TransactionRequestDTO transactionRequestDTO;
    private TransactionStatusResponseDTO transactionStatusResponseDTO;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setSenderUuid("sender-uuid");
        transactionRequestDTO.setReceiverUuid("receiver-uuid");
        transactionRequestDTO.setAmount(100.0);

        transactionStatusResponseDTO = new TransactionStatusResponseDTO();
        transactionStatusResponseDTO.setTransactionUuid("transaction-uuid");

        transaction = new Transaction();
        transaction.setTransactionUuid("transaction-uuid");
        transaction.setSenderUuid("sender-uuid");
        transaction.setReceiverUuid("receiver-uuid");
        transaction.setAmount(100.0);
    }

    @Test
    void createTransaction_Success() throws Exception {
        when(transactionService.createTransaction(any(TransactionRequestDTO.class)))
                .thenReturn(transactionStatusResponseDTO);

        mockMvc.perform(post("/api/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(transactionStatusResponseDTO)));
    }

    @Test
    void createTransaction_Failure() throws Exception {
        when(transactionService.createTransaction(any(TransactionRequestDTO.class)))
                .thenThrow(new RuntimeException("Transaction failed"));

        mockMvc.perform(post("/api/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequestDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getTransaction_Success() throws Exception {
        when(transactionService.fetchTransaction(anyString())).thenReturn(transaction);

        mockMvc.perform(get("/api/transaction/{uuid}", "transaction-uuid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().json(objectMapper.writeValueAsString(transaction)));
    }

    @Test
    void getTransaction_NotFound() throws Exception {
        when(transactionService.fetchTransaction(anyString()))
                .thenThrow(new TransactionNotFoundException("Transaction not found"));

        mockMvc.perform(get("/api/transaction/{uuid}", "123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
