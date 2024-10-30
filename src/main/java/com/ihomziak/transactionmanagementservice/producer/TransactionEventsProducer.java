package com.ihomziak.transactionmanagementservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihomziak.transactionmanagementservice.dto.TransactionRequestDTO;
import com.ihomziak.transactionmanagementservice.entity.Transaction;
import com.ihomziak.transactionmanagementservice.mapper.impl.MapStructureMapperImpl;
import com.ihomziak.transactionmanagementservice.service.RedisService;
import com.ihomziak.transactionmanagementservice.service.TransactionService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class TransactionEventsProducer {

    @Value("${spring.kafka.topic.transfer-transactions-topic}")
    private String TRANSFER_TRANSACTION_TOPIC;

    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final TransactionService transactionService;
    private final RedisService redisService;
    private final MapStructureMapperImpl mapper;


    @Autowired
    public TransactionEventsProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper, TransactionService transactionService, RedisService redisService, MapStructureMapperImpl mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.transactionService = transactionService;
        this.redisService = redisService;
        this.mapper = mapper;
    }

    public CompletableFuture<SendResult<Integer, String>> sendFounds(TransactionRequestDTO transactionRequestDTO) throws JsonProcessingException {
        log.info("Start transaction: {}", transactionRequestDTO);

        log.info("Saving transaction to transaction-db: {}", transactionRequestDTO);
        Transaction transaction = this.transactionService.saveTransaction(transactionRequestDTO);     // store transaction in Redis
        transactionRequestDTO.setTransactionUuid(transaction.getTransactionUuid());

        log.info("Sent transaction: {}", transaction);
        Integer key = transactionRequestDTO.getTransactionEventId();
        String value = objectMapper.writeValueAsString(transactionRequestDTO);

        CompletableFuture<SendResult<Integer, String>> completableFuture = kafkaTemplate.send(TRANSFER_TRANSACTION_TOPIC, key, value);

        completableFuture
                .whenComplete((sendResult, throwable) -> {
                    if (throwable != null) {
                        handleFailure(key, value, throwable);
                    } else {
                        handleSeccess(key, value, sendResult);
                    }
                });

        return completableFuture;
    }

    public CompletableFuture<SendResult<Integer, String>> sendFounds2(TransactionRequestDTO transactionRequestDTO) throws JsonProcessingException {
        log.info("Start transaction: {}", transactionRequestDTO);

        log.info("Saving transaction to transaction-db: {}", transactionRequestDTO);
        Transaction transaction = this.mapper.mapToTransaction(transactionRequestDTO);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionUuid(UUID.randomUUID().toString());

        this.redisService.addRedis(transaction);   // store transaction in Redis

        log.info("Sent transaction: {}", transaction);
        Integer key = transactionRequestDTO.getTransactionEventId();
        String value = objectMapper.writeValueAsString(transaction);

        CompletableFuture<SendResult<Integer, String>> completableFuture = kafkaTemplate.send(TRANSFER_TRANSACTION_TOPIC, key, value);

        completableFuture
                .whenComplete((sendResult, throwable) -> {
                    if (throwable != null) {
                        handleFailure(key, value, throwable);
                    } else {
                        handleSeccess(key, value, sendResult);
                    }
                });

        return completableFuture;
    }

    private void handleSeccess(Integer key, String value, SendResult<Integer, String> sendResult) {
        log.info("Message sent successfully for the key: {} and value: {}, partition is {}",
                key, value, sendResult.getRecordMetadata().partition());
    }

    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Error occurred while sending library event to Kafka: {}", ex.getMessage(), ex);
    }
}
