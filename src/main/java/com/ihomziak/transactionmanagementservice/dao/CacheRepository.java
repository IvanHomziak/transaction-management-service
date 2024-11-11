package com.ihomziak.transactionmanagementservice.dao;

import com.ihomziak.transactionmanagementservice.entity.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CacheRepository extends CrudRepository<Transaction, String> {

    Transaction findTransactionByTransactionUuid(String transactionUuid);
}