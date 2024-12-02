package com.ihomziak.transactionmanagementservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ihomziak.transactioncommon.utils.TransactionStatus;
import com.ihomziak.transactionmanagementservice.dto.TransactionRequestDTO;
import com.ihomziak.transactionmanagementservice.dto.TransactionStatusResponseDTO;
import com.ihomziak.transactionmanagementservice.entity.Transaction;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

public interface TransactionService {

    void processTransactionEventResponse(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException;

    TransactionStatusResponseDTO createTransaction(TransactionRequestDTO transactionDTO) throws JsonProcessingException;

    Transaction fetchTransaction(String id);

    void cancelTransaction(String id);

    List<Transaction> fetchTransactionsByStatus(TransactionStatus status);

    void sendTransactionEvent(Transaction transaction) throws JsonProcessingException;

    void saveToDatabase(Transaction transaction);

    void rollBackTransaction(Transaction transaction);
}