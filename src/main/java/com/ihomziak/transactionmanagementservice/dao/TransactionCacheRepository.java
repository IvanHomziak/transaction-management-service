package com.ihomziak.transactionmanagementservice.dao;

public interface TransactionCacheRepository {

    void saveTransaction(String uuid, String object);

    String findTransactionByIdKey(String transactionId);
}