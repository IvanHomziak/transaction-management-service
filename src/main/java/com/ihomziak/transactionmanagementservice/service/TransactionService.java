package com.ihomziak.transactionmanagementservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ihomziak.transactionmanagementservice.dto.TransactionRequestDTO;
import com.ihomziak.transactionmanagementservice.dto.TransactionResponseDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface TransactionService {

    void processTransaction(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException;

    TransactionResponseDTO sendTransaction(TransactionRequestDTO transactionDTO) throws JsonProcessingException;
}