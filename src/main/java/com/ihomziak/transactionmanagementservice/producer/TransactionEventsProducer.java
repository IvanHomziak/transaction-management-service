package com.ihomziak.transactionmanagementservice.producer;

import com.ihomziak.transactionmanagementservice.dto.AvroTransactionEventRequestDTO;
import com.ihomziak.transactionmanagementservice.dto.TransactionEventRequestDTO;
import com.ihomziak.transactionmanagementservice.entity.Transaction;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class TransactionEventsProducer {

    @Value("${spring.kafka.topic.transfer-transactions-topic}")
    private String TRANSFER_TRANSACTION_TOPIC;

    private final KafkaTemplate<String, AvroTransactionEventRequestDTO> kafkaTemplate;


    @Autowired
    public TransactionEventsProducer(KafkaTemplate<String, AvroTransactionEventRequestDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;

    }

    public void sendTransactionMessage(String transactionKey, AvroTransactionEventRequestDTO transaction) {
        log.info("Start transaction...");

        log.info("Sent transaction, key: {}, transaction: {}", transactionKey, transaction);

        CompletableFuture<SendResult<String, AvroTransactionEventRequestDTO>> completableFuture = kafkaTemplate.send(TRANSFER_TRANSACTION_TOPIC, transactionKey, transaction);

        completableFuture
                .whenComplete((sendResult, throwable) -> {
                    if (throwable != null) {
                        handleFailure(transactionKey, transaction, throwable);
                    } else {
                        handleSuccess(transactionKey, transaction, sendResult);
                    }
                });

    }

    private void handleSuccess(String key, AvroTransactionEventRequestDTO value, SendResult<String, AvroTransactionEventRequestDTO> sendResult) {
        log.info("Message sent successfully for the key: {} and value: {}, partition is {}",
                key, value, sendResult.getRecordMetadata().partition());
    }

    private void handleFailure(String key, AvroTransactionEventRequestDTO value, Throwable ex) {
        log.error("Error occurred while sending transaction event to Kafka. Key: {}, Value: {}, Exception: {}", key, value, ex.getMessage());
    }
}
