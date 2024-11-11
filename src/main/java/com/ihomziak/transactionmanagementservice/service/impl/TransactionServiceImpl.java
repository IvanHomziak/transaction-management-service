package com.ihomziak.transactionmanagementservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihomziak.transactionmanagementservice.dao.CacheRepository;
import com.ihomziak.transactionmanagementservice.dao.TransactionRepository;
import com.ihomziak.transactionmanagementservice.dto.*;
import com.ihomziak.transactionmanagementservice.entity.Transaction;
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
        log.info("Map transactionDTO into transaction entity: {}", transactionDTO);
        Transaction transaction = this.structureMapper.mapTransactionRequestDTOToTransaction(transactionDTO);

        String transactionUuid = UUID.randomUUID().toString();
        log.info("Set transaction UUID: {}", transactionUuid);
        transaction.setTransactionUuid(transactionUuid);

        LocalDateTime transactionDate = LocalDateTime.now();
        log.info("Set transaction date: {}", transactionDate);
        transaction.setTransactionDate(transactionDate);

        log.info("Save transaction into REDIS: {}", transaction);
        cacheRepository.save(transaction);

        TransactionStatus transactionStatus = transaction.getTransactionStatus();
        if (
                // винести в статичний метод перевірку транзакції в окремий класс подумати над назвою стейтфул/стейтлесс класи
                // маю бути стейтлесс
                TransactionStatusChecker.isTransactionStatusNewCompletedOrFailed(transactionStatus)
        ) {
            log.info("Save transaction to data warehouse. transactionUuid: {}, status: {}", transaction.getTransactionUuid(), transactionStatus);
            this.transactionRepository.save(transaction);
        }

        TransactionEventRequestDTO eventRequestDTO = this.structureMapper.mapTransactionToTransactionEventRequestDTO(transaction);
        String transactionMessage = objectMapper.writeValueAsString(eventRequestDTO);
        log.info("Sending transaction message: {}", transactionMessage);
        this.transactionEventsProducer.sendTransactionMessage(1, transactionMessage);

        TransactionStatusResponseDTO transactionStatusResponseDTO = this.structureMapper.mapTransactionToTransactionStatusResponseDTO(transaction);
        log.info("Sending transaction status response DTO: {}", transactionStatusResponseDTO);
        return transactionStatusResponseDTO;
    }

    @Override
    @Transactional
    public void processTransaction(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        log.info("Consumer record: {}", consumerRecord.value());
        TransactionEventResponseDTO responseMessage = objectMapper.readValue(consumerRecord.value(), TransactionEventResponseDTO.class);

        if (responseMessage.getTransactionStatus().equals(TransactionStatus.FAILED)) {
            // додати ексепшин кастомний
            throw new RuntimeException("Transaction FAILED. Message: " + responseMessage.getStatusMessage());
        }

        // Fetch the updateStoredTransaction from the database, ensuring we have the primary key (transactionId). ми маємо тягнути з редіса
        // транзакцію і перевіряти
        Optional<Transaction> currentRedisTransaction = Optional.ofNullable(cacheRepository.findTransactionByTransactionUuid(responseMessage.getTransactionUuid()));
        if (currentRedisTransaction.isEmpty()) {
            throw new TransactionNotFoundException("Transaction with UUID " + responseMessage.getTransactionUuid() + " not found");
        }

        Optional<Transaction>  storedTransaction = Optional.ofNullable(transactionRepository.findTransactionByTransactionUuid(currentRedisTransaction.get().getTransactionUuid()));
        if (storedTransaction.isEmpty()) {
            log.info("Transaction with UUID {} not found in database", responseMessage.getTransactionUuid());
            throw new TransactionNotFoundException("Transaction with UUID " + responseMessage.getTransactionUuid() + " not found");
        }

        Transaction updateStoredTransaction = storedTransaction.get();
        updateStoredTransaction.setTransactionStatus(responseMessage.getTransactionStatus());
        updateStoredTransaction.setLastUpdate(LocalDateTime.now());


        // Save the updated updateStoredTransaction in the database (this will update instead of insert)

        log.info("Update transaction record in REDIS db, updateStoredTransaction: {}", updateStoredTransaction);
        cacheRepository.save(updateStoredTransaction);

        log.info("Update transaction record in MySQL db, updateStoredTransaction: {}", updateStoredTransaction);
        this.transactionRepository.save(updateStoredTransaction);
        log.info("Transaction with UUID {} successfully updated, updateStoredTransaction status: {}", responseMessage.getTransactionUuid(), updateStoredTransaction.getTransactionStatus());
    }

    @Override
    public Transaction getTransaction(String uuid) {
        return this.cacheRepository.findTransactionByTransactionUuid(uuid);
    }
}