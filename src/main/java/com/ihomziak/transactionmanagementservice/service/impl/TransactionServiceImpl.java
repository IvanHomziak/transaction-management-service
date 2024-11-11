package com.ihomziak.transactionmanagementservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihomziak.transactionmanagementservice.dao.TransactionRepository;
import com.ihomziak.transactionmanagementservice.dao.impl.TransactionCacheRepositoryImpl;
import com.ihomziak.transactionmanagementservice.dto.TransactionEventRequestDTO;
import com.ihomziak.transactionmanagementservice.dto.TransactionRequestDTO;
import com.ihomziak.transactionmanagementservice.dto.TransactionResponseDTO;
import com.ihomziak.transactionmanagementservice.dto.TransactionStatusResponseDTO;
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
    private final TransactionCacheRepositoryImpl redisCacheRepository;
    private final TransactionEventsProducer transactionEventsProducer;

    private final ObjectMapper objectMapper;
    private final MapStructureMapperImpl structureMapper;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionCacheRepositoryImpl redisCacheRepository, TransactionEventsProducer transactionEventsProducer, ObjectMapper objectMapper, MapStructureMapperImpl structureMapper) {
        this.transactionRepository = transactionRepository;
        this.redisCacheRepository = redisCacheRepository;
        this.transactionEventsProducer = transactionEventsProducer;
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
        String object = objectMapper.writeValueAsString(transaction);
        this.redisCacheRepository.saveTransaction(transaction.getTransactionUuid(), object);

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

        TransactionResponseDTO transactionResponseDTO = objectMapper.readValue(consumerRecord.value(), TransactionResponseDTO.class);

        if (transactionResponseDTO.getErrorMessage() != null) {
            // додати ексепшин кастомний
            throw new RuntimeException("Transaction response error: " + transactionResponseDTO.getErrorMessage());
        }

        // Fetch the transaction from the database, ensuring we have the primary key (transactionId). ми маємо тягнути з редіса
        // транзакцію і перевіряти
        Optional<Transaction> existingTransactionOpt = Optional.ofNullable(transactionRepository.findTransactionByTransactionUuid(transactionResponseDTO.getTransactionUuid()));

        if (existingTransactionOpt.isEmpty()) {
            log.info("Transaction with UUID {} not found in database", transactionResponseDTO.getTransactionUuid());
            throw new TransactionNotFoundException("Transaction with UUID " + transactionResponseDTO.getTransactionUuid() + " not found");
        }

        Transaction updatedTransaction = existingTransactionOpt.get();
        updatedTransaction.setTransactionStatus(transactionResponseDTO.getTransactionStatus());
        updatedTransaction.setLastUpdate(LocalDateTime.now());

        // Save the updated transaction in the database (this will update instead of insert)
        this.transactionRepository.save(updatedTransaction);

        // Update Redis with the latest transaction data
        String updatedObject = objectMapper.writeValueAsString(updatedTransaction);
        log.info("Update transaction in REDIS db, updatedObject: {}", updatedObject);
        this.redisCacheRepository.saveTransaction(updatedTransaction.getTransactionUuid(), updatedObject);

        log.info("Transaction with UUID {} successfully updated, transaction status: {}", transactionResponseDTO.getTransactionUuid(), updatedTransaction.getTransactionStatus());
    }
}