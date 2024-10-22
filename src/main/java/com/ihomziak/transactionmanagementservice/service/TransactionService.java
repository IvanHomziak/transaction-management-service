package com.ihomziak.transactionmanagementservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ihomziak.transactionmanagementservice.dto.TransactionRequestDTO;
import com.ihomziak.transactionmanagementservice.entity.Transaction;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface TransactionService {

    Transaction saveTransaction(TransactionRequestDTO transactionRequestDTO);

    void processTransactionAnswer(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException;
}