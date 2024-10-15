package com.ihomziak.transactionmanagementservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class KafkaAutoCreateConfig {

    @Value("${spring.kafka.topic.transfer-transactions-name}")
    private String TRANSFER_TRANSACTION_TOPIC;

    @Value("${spring.kafka.topic.account-info-name}")
    private String ACCOUNT_TOPIC;

    @Bean
    public List<NewTopic> topics() {
        List<String> topicNames = List.of(
                TRANSFER_TRANSACTION_TOPIC,
                ACCOUNT_TOPIC
        );
        return topicNames.stream()
                .map(topicName -> TopicBuilder.name(topicName)
                        .partitions(3)
                        .replicas(3)
                        .build())
                .collect(Collectors.toList());
    }
}