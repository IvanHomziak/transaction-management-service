package com.ihomziak.transactionmanagementservice.dao;

public interface RedisCacheRepository {

    void saveToRedis(String uuid, String object);

    String findTransactionByIdKey(String transactionId);
}