package com.ihomziak.bankingapp.transactionms.mapper.impl;

import com.ihomziak.bankingapp.transactionms.dto.TransactionEventRequestDTO;
import com.ihomziak.bankingapp.transactionms.dto.TransactionRequestDTO;
import com.ihomziak.bankingapp.transactionms.dto.TransactionResponseDTO;
import com.ihomziak.bankingapp.transactionms.dto.TransactionStatusResponseDTO;
import com.ihomziak.bankingapp.transactionms.entity.Transaction;
import com.ihomziak.bankingapp.transactionms.mapper.MapStructureMapper;
import org.springframework.stereotype.Component;

@Component
public class MapStructureMapperImpl implements MapStructureMapper {

    @Override
    public Transaction mapTransactionRequestDTOToTransaction(TransactionRequestDTO transactionRequestDTO) {
        if (transactionRequestDTO == null) {
            return null;
        }

        Transaction transaction = new Transaction();
        transaction.setSenderUuid(transactionRequestDTO.getSenderUuid());
        transaction.setReceiverUuid(transactionRequestDTO.getReceiverUuid());
        transaction.setAmount(transactionRequestDTO.getAmount());
        transaction.setTransactionStatus(transactionRequestDTO.getTransactionStatus());
        return transaction;
    }

    @Override
    public TransactionResponseDTO mapTransactionToTransactionResponseDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        return TransactionResponseDTO.builder()
                .transactionUuid(transaction.getTransactionUuid())
                .transactionStatus(transaction.getTransactionStatus())
                .build();
    }

    @Override
    public TransactionStatusResponseDTO mapTransactionToTransactionStatusResponseDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        return TransactionStatusResponseDTO.builder()
                .transactionUuid(transaction.getTransactionUuid())
                .startTransactionTime(transaction.getTransactionDate())
                .build();
    }

    @Override
    public TransactionEventRequestDTO mapTransactionToTransactionEventRequestDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        return TransactionEventRequestDTO.builder()
                .senderUuid(transaction.getSenderUuid())
                .receiverUuid(transaction.getReceiverUuid())
                .amount(transaction.getAmount())
                .transactionStatus(transaction.getTransactionStatus())
                .transactionUuid(transaction.getTransactionUuid())
                .build();
    }
}