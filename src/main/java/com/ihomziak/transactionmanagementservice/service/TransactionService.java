package com.ihomziak.transactionmanagementservice.service;

import com.ihomziak.transactionmanagementservice.dto.TransactionRequestDTO;
import com.ihomziak.transactionmanagementservice.dto.TransactionResponseDTO;

public interface TransactionService {

    TransactionResponseDTO processTransaction(TransactionRequestDTO transactionRequestDTO);
}
