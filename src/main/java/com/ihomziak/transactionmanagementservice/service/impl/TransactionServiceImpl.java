package com.ihomziak.transactionmanagementservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihomziak.transactionmanagementservice.dao.TransactionRepository;
import com.ihomziak.transactionmanagementservice.dao.impl.RedisCacheRepositoryImpl;
import com.ihomziak.transactionmanagementservice.dto.TransactionRequestDTO;
import com.ihomziak.transactionmanagementservice.dto.TransactionResponseDTO;
import com.ihomziak.transactionmanagementservice.entity.Transaction;
import com.ihomziak.transactionmanagementservice.exception.TransactionNotFoundException;
import com.ihomziak.transactionmanagementservice.mapper.impl.MapStructureMapperImpl;
import com.ihomziak.transactionmanagementservice.producer.TransactionEventsProducer;
import com.ihomziak.transactionmanagementservice.service.TransactionService;
import com.ihomziak.transactionmanagementservice.utils.TransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final RedisCacheRepositoryImpl redisCacheRepository;
    private final TransactionEventsProducer transactionEventsProducer;

    private final ObjectMapper objectMapper;
    private final MapStructureMapperImpl structureMapper;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, RedisCacheRepositoryImpl redisCacheRepository, TransactionEventsProducer transactionEventsProducer, ObjectMapper objectMapper, MapStructureMapperImpl structureMapper) {
        this.transactionRepository = transactionRepository;
        this.redisCacheRepository = redisCacheRepository;
        this.transactionEventsProducer = transactionEventsProducer;
        this.objectMapper = objectMapper;
        this.structureMapper = structureMapper;
    }

    @Override
    public TransactionResponseDTO sendTransaction(TransactionRequestDTO transactionDTO) throws JsonProcessingException {
        log.info("Save transaction into REDIS: {}", transactionDTO);
        Transaction transaction = this.structureMapper.mapTransactionRequestDTOToTransaction(transactionDTO);
        transaction.setTransactionUuid(UUID.randomUUID().toString());
        transaction.setTransactionDate(LocalDateTime.now());

        String object = objectMapper.writeValueAsString(transaction);
        this.redisCacheRepository.saveToRedis(transaction.getTransactionUuid(), object);

        log.info("Sending transaction request: {}", transactionDTO);
        this.transactionEventsProducer.sendTransactionMessage(transactionDTO.getTransactionEventId(), object);

        if (
                transaction.getTransactionStatus().equals(TransactionStatus.NEW) ||
                transaction.getTransactionStatus().equals(TransactionStatus.COMPLETED) ||
                transaction.getTransactionStatus().equals(TransactionStatus.FAILED)
        ) {
            log.info("Save transaction to data warehouse. transactionUuid: {}, status: {}", transaction.getTransactionUuid(), transaction.getTransactionStatus());
            this.transactionRepository.save(transaction);
        }

        return this.structureMapper.mapTransactionToTransactionResponseDTO(transaction);
    }

    @Override
    public void processTransaction(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        log.info("Consumer record: {}", consumerRecord.value());

        TransactionResponseDTO transactionResponseDTO = objectMapper.readValue(consumerRecord.value(), TransactionResponseDTO.class);

        if (transactionResponseDTO.getErrorMessage() != null) {
            throw new RuntimeException("Transaction response error: " + transactionResponseDTO.getErrorMessage());
        }

        String dbObject = this.redisCacheRepository.findTransactionByIdKey(transactionResponseDTO.getTransactionUuid());

        if (dbObject == null || dbObject.isEmpty()) {
            log.info("Transaction with UUID {} not found", transactionResponseDTO.getTransactionUuid());
            throw new TransactionNotFoundException("Transaction with UUID " + transactionResponseDTO.getTransactionUuid() + " not found");
        }

        Transaction transaction = objectMapper.readValue(dbObject, Transaction.class);
        transaction.setTransactionStatus(transactionResponseDTO.getTransactionStatus());
        transaction.setLastUpdate(LocalDateTime.now());

        String updatedObject = objectMapper.writeValueAsString(transaction);
        log.info("Update transaction in REDIS db, updatedObject: {}", updatedObject);

        this.redisCacheRepository.saveToRedis(transactionResponseDTO.getTransactionUuid(), dbObject);

        log.info("Save transaction into data warehouse");
        this.transactionRepository.save(transaction);
        log.info("Transaction with UUID {} successfully updated, transaction status: {}", transactionResponseDTO.getTransactionUuid(), transaction.getTransactionStatus());
    }
}