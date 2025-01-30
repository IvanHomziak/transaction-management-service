package com.ihomziak.bankingapp.transactionms.consumer.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ihomziak.bankingapp.transactionms.service.impl.TransactionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransactionEventConsumer {

    private final TransactionServiceImpl transactionServiceImpl;

    @Autowired
    public TransactionEventConsumer(TransactionServiceImpl transactionServiceImpl) {
        this.transactionServiceImpl = transactionServiceImpl;
    }

    @KafkaListener(topics = {"${spring.kafka.topic.transaction-results-topic}"})
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        log.info("Received ConsumerRecord: {}", consumerRecord.value());
        this.transactionServiceImpl.processTransactionEventResponse(consumerRecord);
    }
}