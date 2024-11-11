package com.ihomziak.transactionmanagementservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ihomziak.transactionmanagementservice.dto.TransactionRequestDTO;
import com.ihomziak.transactionmanagementservice.dto.TransactionStatusResponseDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface TransactionService {

    void processTransaction(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException;

    TransactionStatusResponseDTO createTransaction(TransactionRequestDTO transactionDTO) throws JsonProcessingException;
}