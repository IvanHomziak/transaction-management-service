package com.ihomziak.transactionmanagementservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihomziak.transactionmanagementservice.dao.TransactionRepository;
import com.ihomziak.transactionmanagementservice.dto.TransactionRequestDTO;
import com.ihomziak.transactionmanagementservice.dto.TransactionResponseDTO;
import com.ihomziak.transactionmanagementservice.entity.Transaction;
import com.ihomziak.transactionmanagementservice.exception.TransactionNotFoundException;
import com.ihomziak.transactionmanagementservice.service.RedisService;
import com.ihomziak.transactionmanagementservice.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final ObjectMapper objectMapper;
    private final RedisService redisService;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, ObjectMapper objectMapper, RedisService redisService) {
        this.transactionRepository = transactionRepository;
        this.objectMapper = objectMapper;
        this.redisService = redisService;
    }

    @Override
    public Transaction saveTransaction(TransactionRequestDTO transactionRequestDTO) {
        Transaction transaction = new Transaction();
        transaction.setTransactionUuid(UUID.randomUUID().toString());
        transaction.setSenderUuid(transactionRequestDTO.getSenderUuid());
        transaction.setReceiverUuid(transactionRequestDTO.getReceiverUuid());
        transaction.setAmount(transactionRequestDTO.getAmount());
        transaction.setTransactionDate(LocalDateTime.now());

        transaction.setTransactionStatus(transactionRequestDTO.getTransactionStatus());
        transaction.setTransactionType(transactionRequestDTO.getTransactionType());

        return transactionRepository.save(transaction);
    }

    @Override
    public void processTransactionAnswer(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        log.info("Consumer record: {}", consumerRecord.value());

        TransactionResponseDTO transactionResponseDTO = objectMapper.readValue(consumerRecord.value(), TransactionResponseDTO.class);

        Optional<Transaction> dbTransaction = this.transactionRepository.findTransactionByTransactionUuid(transactionResponseDTO.getTransactionUuid());

        if (dbTransaction.isEmpty()) {
            log.info("Transaction with UUID {} not found", transactionResponseDTO.getTransactionUuid());
            throw new TransactionNotFoundException("Transaction with UUID " + transactionResponseDTO.getTransactionUuid() + " not found");
        }

        Transaction transaction = dbTransaction.get();
        transaction.setTransactionStatus(transactionResponseDTO.getTransactionStatus());

        transaction.setLastUpdate(LocalDateTime.now());
        this.transactionRepository.save(transaction);
        log.info("Transaction with UUID {} successfully updated, transaction status: {}", transactionResponseDTO.getTransactionUuid(), transaction.getTransactionStatus());
    }

    @Override
    public void processTransactionAnswer2(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        log.info("Consumer record: {}", consumerRecord.value());

        TransactionResponseDTO transactionResponseDTO = objectMapper.readValue(consumerRecord.value(), TransactionResponseDTO.class);
        String json = this.redisService.getValue(transactionResponseDTO.getTransactionUuid());

        if (json == null || json.isEmpty()) {
            log.info("Transaction with UUID {} not found", transactionResponseDTO.getTransactionUuid());
            throw new TransactionNotFoundException("Transaction with UUID " + transactionResponseDTO.getTransactionUuid() + " not found");
        }

        Transaction transaction = objectMapper.readValue(json, Transaction.class);
        transaction.setTransactionStatus(transactionResponseDTO.getTransactionStatus());

        transaction.setLastUpdate(LocalDateTime.now());

        this.redisService.addRedis(transaction);
        this.transactionRepository.save(transaction);
        log.info("Transaction with UUID {} successfully updated, transaction status: {}", transactionResponseDTO.getTransactionUuid(), transaction.getTransactionStatus());
    }
}