package com.ihomziak.bankingapp.transactionms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ihomziak.bankingapp.common.utils.TransactionStatus;
import com.ihomziak.bankingapp.transactionms.dto.TransactionRequestDTO;
import com.ihomziak.bankingapp.transactionms.dto.TransactionStatusResponseDTO;
import com.ihomziak.bankingapp.transactionms.entity.Transaction;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

public interface TransactionService {

    void processTransactionEventResponse(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException;

    TransactionStatusResponseDTO createTransaction(TransactionRequestDTO transactionDTO) throws JsonProcessingException;

    Transaction fetchTransaction(String id);

    void cancelTransaction(String id);

    List<Transaction> fetchTransactionsByStatus(TransactionStatus status);

    void sendTransactionEvent(Transaction transaction) throws JsonProcessingException;

    void saveTransaction(Transaction transaction);
}