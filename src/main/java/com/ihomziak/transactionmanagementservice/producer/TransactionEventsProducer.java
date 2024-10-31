package com.ihomziak.transactionmanagementservice.producer;

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

    private final KafkaTemplate<Integer, String> kafkaTemplate;


    @Autowired
    public TransactionEventsProducer(KafkaTemplate<Integer, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;

    }

    public void sendTransactionMessage(int transactionKey, String transactionMessage) {
        log.info("Start transaction...");

        log.info("Sent transaction, key: {}, transactionMessage: {}", transactionKey, transactionMessage);

        CompletableFuture<SendResult<Integer, String>> completableFuture = kafkaTemplate.send(TRANSFER_TRANSACTION_TOPIC, transactionKey, transactionMessage);

        completableFuture
                .whenComplete((sendResult, throwable) -> {
                    if (throwable != null) {
                        handleFailure(transactionKey, transactionMessage, throwable);
                    } else {
                        handleSuccess(transactionKey, transactionMessage, sendResult);
                    }
                });

    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> sendResult) {
        log.info("Message sent successfully for the key: {} and value: {}, partition is {}",
                key, value, sendResult.getRecordMetadata().partition());
    }

    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Error occurred while sending transaction event to Kafka. Key: {}, Value: {}, Exception: {}", key, value, ex.getMessage());
    }
}
