package com.ihomziak.transactionmanagementservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihomziak.transactionmanagementservice.dao.CacheRepository;
import com.ihomziak.transactionmanagementservice.dao.TransactionRepository;
import com.ihomziak.transactionmanagementservice.dto.*;
import com.ihomziak.transactionmanagementservice.entity.Transaction;
import com.ihomziak.transactionmanagementservice.exception.TransactionCancellationException;
import com.ihomziak.transactionmanagementservice.exception.TransactionFailedException;
import com.ihomziak.transactionmanagementservice.exception.TransactionNotFoundException;
import com.ihomziak.transactionmanagementservice.mapper.impl.MapStructureMapperImpl;
import com.ihomziak.transactionmanagementservice.producer.TransactionEventsProducer;
import com.ihomziak.transactionmanagementservice.service.TransactionService;
import com.ihomziak.transactioncommon.utils.TransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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

        saveToDatabase(transaction);

        return structureMapper.mapTransactionToTransactionStatusResponseDTO(transaction);
    }

    @Override
    @Transactional
    public void saveToDatabase(Transaction transaction) {
        log.info("Saving transaction to MySQL: transactionUuid={}, status={}", transaction.getTransactionUuid(), transaction.getTransactionStatus());
        transactionRepository.save(transaction);
    }

    @Override
    public void rollBackTransaction(Transaction transaction) {
        log.info("Rolling back transaction: {}", transaction);


        // TODO:
    }

    @Override
    public void sendTransactionEvent(Transaction transaction) throws JsonProcessingException {
        TransactionEventRequestDTO eventRequestDTO = structureMapper.mapTransactionToTransactionEventRequestDTO(transaction);
        String transactionMessage = objectMapper.writeValueAsString(eventRequestDTO);
        log.info("Sending transaction event message: {}", transactionMessage);
        transactionEventsProducer.sendTransactionMessage(1, transactionMessage);
    }

    @Override
    @Transactional
    public void processTransactionEventResponse(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException, TransactionFailedException {
        log.info("Processing consumer record: {}", consumerRecord.value());
        TransactionEventResponseDTO responseMessage = deserializeConsumerRecord(consumerRecord);

        Transaction storedTransaction = fetchTransactionFromDatabase(responseMessage.getTransactionUuid());

        updateAndSaveTransaction(storedTransaction, responseMessage.getTransactionStatus());
    }

    @Override
    @Transactional
    public Transaction fetchTransaction(String uuid) {
        Optional<Transaction> transactionOptional = Optional.ofNullable(cacheRepository.findTransactionByTransactionUuid(uuid));

        if (transactionOptional.isEmpty()) {
            throw new TransactionNotFoundException("Transaction with UUID " + uuid + " not found in Redis database");
        }

        return transactionOptional.get();
    }

    @Override
    @Transactional
    public void cancelTransaction(String id) {
        log.info("Rolling back transaction with id: {}", id);

        Transaction transaction = this.transactionRepository.findTransactionByTransactionUuid(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction with UUID " + id + " not found in database"));

        if (transaction.getTransactionStatus().equals(TransactionStatus.STARTED) ||
                transaction.getTransactionStatus().equals(TransactionStatus.CANCELED)) {
            throw new TransactionCancellationException("Transaction with UUID " + id + "is '" + transaction.getTransactionStatus() + "'. Transaction cannot be canceled.");

        } else {
            transaction.setTransactionStatus(TransactionStatus.CANCELED);
            transactionRepository.save(transaction);
        }
    }

    @Override
    public List<Transaction> fetchTransactionsByStatus(TransactionStatus status) {
        log.info("Retrieving 'CREATED' transactions");

        return this.transactionRepository.findTransactionByTransactionStatus(status)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction with status '" + status + "' not found"));
    }

    private Transaction prepareTransaction(TransactionRequestDTO transactionDTO) {
        Transaction transaction = structureMapper.mapTransactionRequestDTOToTransaction(transactionDTO);
        transaction.setTransactionUuid(UUID.randomUUID().toString());
        transaction.setTransactionDate(LocalDateTime.now());

        if (transaction.getTransactionStatus().equals(TransactionStatus.NEW)) {
            transaction.setTransactionStatus(TransactionStatus.CREATED);
        }
        log.info("Prepared transaction entity: {}", transaction);
        return transaction;
    }

    private void updateAndSaveTransaction(Transaction transaction, TransactionStatus status) {
        transaction.setTransactionStatus(status);
        transaction.setLastUpdate(LocalDateTime.now());

        log.info("Updating transaction in MySQL: {}", transaction);
        transactionRepository.save(transaction);

        log.info("Transaction updated successfully: UUID={}, status={}", transaction.getTransactionUuid(), transaction.getTransactionStatus());
    }

    private TransactionEventResponseDTO deserializeConsumerRecord(ConsumerRecord<Integer, String> consumerRecord)
            throws JsonProcessingException {
        return objectMapper.readValue(consumerRecord.value(), TransactionEventResponseDTO.class);
    }

    private Transaction fetchTransactionFromDatabase(String transactionUuid) {
        return transactionRepository.findTransactionByTransactionUuid(transactionUuid)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction with UUID " + transactionUuid + " not found in MySQL database"));
    }
}
