package com.ihomziak.transactionmanagementservice.mapper;

import com.ihomziak.transactionmanagementservice.dto.TransactionRequestDTO;
import com.ihomziak.transactionmanagementservice.dto.TransactionResponseDTO;
import com.ihomziak.transactionmanagementservice.entity.Transaction;

public interface MapStructureMapper {

    Transaction mapTransactionRequestDTOToTransaction(TransactionRequestDTO transactionTMS);

    TransactionResponseDTO mapTransactionToTransactionResponseDTO(Transaction transaction);
}
