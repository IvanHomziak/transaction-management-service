package com.ihomziak.bankingapp.transactionms.mapper;

import com.ihomziak.bankingapp.transactionms.dto.TransactionEventRequestDTO;
import com.ihomziak.bankingapp.transactionms.dto.TransactionRequestDTO;
import com.ihomziak.bankingapp.transactionms.dto.TransactionResponseDTO;
import com.ihomziak.bankingapp.transactionms.dto.TransactionStatusResponseDTO;
import com.ihomziak.bankingapp.transactionms.entity.Transaction;

public interface MapStructureMapper {

    Transaction mapTransactionRequestDTOToTransaction(TransactionRequestDTO transactionTMS);

    TransactionResponseDTO mapTransactionToTransactionResponseDTO(Transaction transaction);

    TransactionStatusResponseDTO mapTransactionToTransactionStatusResponseDTO(Transaction transaction);

    TransactionEventRequestDTO mapTransactionToTransactionEventRequestDTO(Transaction transaction);
}