package com.ihomziak.transactionmanagementservice.mapper.impl;

import com.ihomziak.transactionmanagementservice.dto.*;
import com.ihomziak.transactionmanagementservice.entity.Transaction;
import com.ihomziak.transactionmanagementservice.mapper.MapStructureMapper;
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
                .finishedTransactionTime(transaction.getLastUpdate())
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

    @Override
    public AvroTransactionEventRequestDTO mapTransactionToAvroTransactionEventRequestDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        AvroTransactionEventRequestDTO avroTransactionEventRequestDTO = new AvroTransactionEventRequestDTO();

        avroTransactionEventRequestDTO.setTransactionUuid(transaction.getTransactionUuid());
        avroTransactionEventRequestDTO.setSenderUuid(transaction.getSenderUuid());
        avroTransactionEventRequestDTO.setReceiverUuid(transaction.getReceiverUuid());
        avroTransactionEventRequestDTO.setAmount(transaction.getAmount());

        return avroTransactionEventRequestDTO;
    }
}