package com.ihomziak.transactionmanagementservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihomziak.transactionmanagementservice.dto.TransactionRequestDTO;
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

    @Value("${spring.kafka.topic.transfer-transactions-name}")
    private String topic;

    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    @Autowired
    public TransactionEventsProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<SendResult<Integer, String>> sendTransaction(TransactionRequestDTO transactionRequestDTO) throws JsonProcessingException {
        log.info(transactionRequestDTO.toString());
        var key = transactionRequestDTO.getTransactionEventId();
        var value = objectMapper.writeValueAsString(transactionRequestDTO);

        var completableFuture = kafkaTemplate.send(topic, key, value);

        return completableFuture
                .whenComplete((sendResult, throwable) -> {
                    if (throwable != null) {
                        handleFailure(key,value, throwable);
                    } else {
                        handleSeccess(key, value, sendResult);
                    }
                });
    }

    private void handleSeccess(Integer key, String value, SendResult<Integer, String> sendResult) {
        log.info("Message sent successfully for the key: {} and value: {}, partition is {}",
                key, value, sendResult.getRecordMetadata().partition());
    }

    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Error occurred while sending library event to Kafka: {}", ex.getMessage(), ex);
    }
}
