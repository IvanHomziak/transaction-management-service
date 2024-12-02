package com.ihomziak.transactionmanagementservice.dao;

import com.ihomziak.transactioncommon.utils.TransactionStatus;
import com.ihomziak.transactionmanagementservice.entity.Transaction;
import org.apache.kafka.common.quota.ClientQuotaAlteration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findTransactionByTransactionUuid(String transactionUuid);

    Optional<List<Transaction>> findTransactionByTransactionStatus(TransactionStatus transactionStatus);

}
