package com.ihomziak.transactionmanagementservice.dao;

import com.ihomziak.transactionmanagementservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findTransactionByTransactionUuid(String uuid);
}
