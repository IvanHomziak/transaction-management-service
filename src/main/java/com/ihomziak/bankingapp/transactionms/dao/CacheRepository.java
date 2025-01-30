package com.ihomziak.bankingapp.transactionms.dao;

import com.ihomziak.bankingapp.transactionms.entity.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CacheRepository extends CrudRepository<Transaction, String> {

    Transaction findTransactionByTransactionUuid(String transactionUuid);
}