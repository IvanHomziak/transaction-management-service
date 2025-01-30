package com.ihomziak.bankingapp.transactionms.dao;

import com.ihomziak.bankingapp.common.utils.TransactionStatus;
import com.ihomziak.bankingapp.transactionms.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findTransactionByTransactionUuid(String transactionUuid);

    Optional<List<Transaction>> findTransactionByTransactionStatus(TransactionStatus transactionStatus);

}
