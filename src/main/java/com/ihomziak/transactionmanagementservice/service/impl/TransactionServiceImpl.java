package com.ihomziak.transactionmanagementservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihomziak.transactionmanagementservice.dao.TransactionRepository;
import com.ihomziak.transactionmanagementservice.dto.*;
import com.ihomziak.transactionmanagementservice.entity.Transaction;
import com.ihomziak.transactionmanagementservice.exception.TransactionCancellationException;
import com.ihomziak.transactionmanagementservice.exception.TransactionNotFoundException;
import com.ihomziak.transactionmanagementservice.mapper.impl.MapStructureMapperImpl;
import com.ihomziak.transactionmanagementservice.producer.TransactionEventsProducer;
import com.ihomziak.transactionmanagementservice.service.TransactionService;
import com.ihomziak.transactioncommon.utils.TransactionStatus;
import com.ihomziak.transactionmanagementservice.utils.TransactionStatusChecker;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionEventsProducer transactionEventsProducer;
    private final ObjectMapper objectMapper;
    private final MapStructureMapperImpl structureMapper;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  TransactionEventsProducer transactionEventsProducer,
                                  ObjectMapper objectMapper,
                                  MapStructureMapperImpl structureMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionEventsProducer = transactionEventsProducer;
        this.objectMapper = objectMapper;
        this.structureMapper = structureMapper;
    }

    @Override
    @Transactional
    public TransactionStatusResponseDTO createTransaction(TransactionRequestDTO transactionDTO) {
        log.info("Creating transaction for request: {}", transactionDTO);

        Transaction transaction = prepareTransaction(transactionDTO);
        saveTransaction(transaction);

        return structureMapper.mapTransactionToTransactionStatusResponseDTO(transaction);
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        log.info("Saving transaction to database: UUID={}, Status={}", transaction.getTransactionUuid(), transaction.getTransactionStatus());
        transactionRepository.save(transaction);
    }

    @Override
    public void sendTransactionEvent(Transaction transaction) throws JsonProcessingException {
        TransactionEventRequestDTO eventRequestDTO = structureMapper.mapTransactionToTransactionEventRequestDTO(transaction);
        String transactionMessage = objectMapper.writeValueAsString(eventRequestDTO);

        log.info("Sending transaction event: {}", transactionMessage);
        transactionEventsProducer.sendTransactionMessage(1, transactionMessage);
    }

    @Override
    @Transactional
    public void processTransactionEventResponse(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        log.info("Processing consumer record: {}", consumerRecord.value());

        TransactionEventResponseDTO responseMessage = deserializeConsumerRecord(consumerRecord);
        Transaction transaction = fetchTransactionFromDatabase(responseMessage.getTransactionUuid());

        updateTransactionStatus(transaction, responseMessage.getTransactionStatus());
    }

    @Override
    @Transactional(readOnly = true)
    public Transaction fetchTransaction(String uuid) {
        return this.transactionRepository.findTransactionByTransactionUuid(uuid)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction with UUID " + uuid + " not found in Redis cache"));
    }

    @Override
    @Transactional
    public void cancelTransaction(String id) {
        log.info("Cancelling transaction with id: {}", id);

        Transaction transaction = fetchTransactionFromDatabase(id);

        if (TransactionStatusChecker.isTransactionStatusStartedCompletedCanceled(transaction.getTransactionStatus())) {
            throw new TransactionCancellationException(String.format(
                    "Transaction with UUID %s is '%s'. Cannot cancel.", id, transaction.getTransactionStatus()));
        }

        transaction.setTransactionStatus(TransactionStatus.CANCELED);
        saveTransaction(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> fetchTransactionsByStatus(TransactionStatus status) {
        log.info("Fetching transactions with status: {}", status);

        return transactionRepository.findTransactionByTransactionStatus(status)
                .orElseThrow(() -> new TransactionNotFoundException("No transactions found with status '" + status + "'"));
    }

    private Transaction prepareTransaction(TransactionRequestDTO transactionDTO) {
        Transaction transaction = structureMapper.mapTransactionRequestDTOToTransaction(transactionDTO);
        transaction.setTransactionUuid(UUID.randomUUID().toString());
        transaction.setTransactionDate(LocalDateTime.now());

        if (TransactionStatus.NEW.equals(transaction.getTransactionStatus())) {
            transaction.setTransactionStatus(TransactionStatus.CREATED);
        }

        log.info("Prepared transaction: {}", transaction);
        return transaction;
    }

    private void updateTransactionStatus(Transaction transaction, TransactionStatus status) {
        transaction.setTransactionStatus(status);
        transaction.setLastUpdate(LocalDateTime.now());

        log.info("Updating transaction: UUID={}, New Status={}", transaction.getTransactionUuid(), status);
        saveTransaction(transaction);
    }

    private TransactionEventResponseDTO deserializeConsumerRecord(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        return objectMapper.readValue(consumerRecord.value(), TransactionEventResponseDTO.class);
    }

    private Transaction fetchTransactionFromDatabase(String transactionUuid) {
        return transactionRepository.findTransactionByTransactionUuid(transactionUuid)
                .orElseThrow(() -> new TransactionNotFoundException(
                        "Transaction with UUID " + transactionUuid + " not found in the database"));
    }
}
