package com.ihomziak.transactionmanagementservice.mapper.impl;

import com.ihomziak.transactionmanagementservice.dto.TransactionRequestDTO;
import com.ihomziak.transactionmanagementservice.dto.TransactionResponseDTO;
import com.ihomziak.transactionmanagementservice.entity.Transaction;
import com.ihomziak.transactionmanagementservice.mapper.MapStructureMapper;
import org.springframework.stereotype.Component;

@Component
public class MapStructureMapperImpl implements MapStructureMapper {

    @Override
    public Transaction mapTransactionRequestDTOToTransaction(TransactionRequestDTO transactionRequestDTO) {
        Transaction transaction = new Transaction();
        transaction.setSenderUuid(transactionRequestDTO.getSenderUuid());
        transaction.setReceiverUuid(transactionRequestDTO.getReceiverUuid());
        transaction.setAmount(transactionRequestDTO.getAmount());
        transaction.setTransactionStatus(transactionRequestDTO.getTransactionStatus());
        transaction.setTransactionType(transactionRequestDTO.getTransactionType());

        return transaction;
    }

    @Override
    public TransactionResponseDTO mapTransactionToTransactionResponseDTO(Transaction transaction) {
        TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO();

        transactionResponseDTO.setTransactionUuid(transaction.getTransactionUuid());
        transactionResponseDTO.setTransactionStatus(transaction.getTransactionStatus());
        return transactionResponseDTO;
    }
}
