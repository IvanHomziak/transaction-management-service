package com.ihomziak.transactionmanagementservice.mapper;

import com.ihomziak.transactionmanagementservice.dto.*;
import com.ihomziak.transactionmanagementservice.entity.Transaction;

public interface MapStructureMapper {

    Transaction mapTransactionRequestDTOToTransaction(TransactionRequestDTO transactionTMS);

    TransactionResponseDTO mapTransactionToTransactionResponseDTO(Transaction transaction);

    TransactionStatusResponseDTO mapTransactionToTransactionStatusResponseDTO(Transaction transaction);

    TransactionEventRequestDTO mapTransactionToTransactionEventRequestDTO(Transaction transaction);

    AvroTransactionEventRequestDTO mapTransactionToAvroTransactionEventRequestDTO(Transaction transaction);
}
