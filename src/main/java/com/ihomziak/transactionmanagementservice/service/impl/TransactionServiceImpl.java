package com.ihomziak.transactionmanagementservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihomziak.transactionmanagementservice.dao.CacheRepository;
import com.ihomziak.transactionmanagementservice.dao.TransactionRepository;
import com.ihomziak.transactionmanagementservice.dto.*;
import com.ihomziak.transactionmanagementservice.entity.Transaction;
import com.ihomziak.transactionmanagementservice.exception.TransactionFailedException;
import com.ihomziak.transactionmanagementservice.exception.TransactionNotFoundException;
import com.ihomziak.transactionmanagementservice.mapper.impl.MapStructureMapperImpl;
import com.ihomziak.transactionmanagementservice.producer.TransactionEventsProducer;
import com.ihomziak.transactionmanagementservice.service.TransactionService;
import com.ihomziak.transactioncommon.TransactionStatus;
import com.ihomziak.transactionmanagementservice.utils.TransactionStatusChecker;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionEventsProducer transactionEventsProducer;
    private final CacheRepository cacheRepository;
    private final ObjectMapper objectMapper;
    private final MapStructureMapperImpl structureMapper;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionEventsProducer transactionEventsProducer, CacheRepository cacheRepository, ObjectMapper objectMapper, MapStructureMapperImpl structureMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionEventsProducer = transactionEventsProducer;
        this.cacheRepository = cacheRepository;
        this.objectMapper = objectMapper;
        this.structureMapper = structureMapper;
    }

    @Override
    @Transactional
    public TransactionStatusResponseDTO createTransaction(TransactionRequestDTO transactionDTO) throws JsonProcessingException {
        log.info("Creating transaction for request: {}", transactionDTO);

        Transaction transaction = prepareTransaction(transactionDTO);

        saveToCache(transaction);

        if (TransactionStatusChecker.isTransactionStatusNewCompletedOrFailed(transaction.getTransactionStatus())) {
            saveToDatabase(transaction);
        }

        sendTransactionEvent(transaction);

        return mapToTransactionStatusResponse(transaction);
    }

    private Transaction prepareTransaction(TransactionRequestDTO transactionDTO) {
        Transaction transaction = structureMapper.mapTransactionRequestDTOToTransaction(transactionDTO);
        transaction.setTransactionUuid(UUID.randomUUID().toString());
        transaction.setTransactionDate(LocalDateTime.now());
        log.info("Prepared transaction entity: {}", transaction);
        return transaction;
    }

    private void saveToCache(Transaction transaction) {
        log.info("Saving transaction to Redis: {}", transaction);
        cacheRepository.save(transaction);
    }

    private void saveToDatabase(Transaction transaction) {
        log.info("Saving transaction to MySQL: transactionUuid={}, status={}", transaction.getTransactionUuid(), transaction.getTransactionStatus());
        transactionRepository.save(transaction);
    }

    private void sendTransactionEvent(Transaction transaction) throws JsonProcessingException {
        TransactionEventRequestDTO eventRequestDTO = structureMapper.mapTransactionToTransactionEventRequestDTO(transaction);
        String transactionMessage = objectMapper.writeValueAsString(eventRequestDTO);
        log.info("Sending transaction event message: {}", transactionMessage);
        transactionEventsProducer.sendTransactionMessage(1, transactionMessage);
    }

    private TransactionStatusResponseDTO mapToTransactionStatusResponse(Transaction transaction) {
        TransactionStatusResponseDTO responseDTO = structureMapper.mapTransactionToTransactionStatusResponseDTO(transaction);
        log.info("Mapped transaction status response: {}", responseDTO);
        return responseDTO;
    }

    @Override
    @Transactional
    public void processTransaction(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException, TransactionFailedException {
        log.info("Processing consumer record: {}", consumerRecord.value());
        TransactionEventResponseDTO responseMessage = deserializeConsumerRecord(consumerRecord);

        Transaction redisTransaction = fetchTransactionFromRedis(responseMessage.getTransactionUuid());
        Transaction storedTransaction = fetchTransactionFromDatabase(redisTransaction.getTransactionUuid());

        updateTransactionStatus(storedTransaction, responseMessage.getTransactionStatus());
    }

    private TransactionEventResponseDTO deserializeConsumerRecord(ConsumerRecord<Integer, String> consumerRecord)
            throws JsonProcessingException {
        return objectMapper.readValue(consumerRecord.value(), TransactionEventResponseDTO.class);
    }

    private boolean isTransactionFailed(TransactionEventResponseDTO responseMessage) {
        return TransactionStatus.FAILED.equals(responseMessage.getTransactionStatus());
    }

    private Transaction fetchTransactionFromRedis(String transactionUuid) {
        return Optional.ofNullable(cacheRepository.findTransactionByTransactionUuid(transactionUuid))
                .orElseThrow(() -> new TransactionNotFoundException("Transaction with UUID " + transactionUuid + " not found in Redis"));
    }

    private Transaction fetchTransactionFromDatabase(String transactionUuid) {
        return transactionRepository.findTransactionByTransactionUuid(transactionUuid)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction with UUID " + transactionUuid + " not found in MySQL database"));
    }

    private void updateTransactionStatus(Transaction transaction, TransactionStatus status) {
        transaction.setTransactionStatus(status);
        transaction.setLastUpdate(LocalDateTime.now());

        log.info("Updating transaction in MySQL: {}", transaction);
        transactionRepository.save(transaction);

        log.info("Updating transaction in Redis: {}", transaction);
        cacheRepository.save(transaction);

        log.info("Transaction updated successfully: UUID={}, status={}", transaction.getTransactionUuid(), transaction.getTransactionStatus());
    }

    @Override
    public Transaction getTransaction(String uuid) {
        Optional<Transaction> transactionOptional = Optional.ofNullable(cacheRepository.findTransactionByTransactionUuid(uuid));

        if (transactionOptional.isEmpty()) {
            throw new TransactionNotFoundException("Transaction with UUID " + uuid + " not found in Redis database");
        }

        return transactionOptional.get();
    }
}
