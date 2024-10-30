package com.ihomziak.transactionmanagementservice.mapper;

import com.ihomziak.transactionmanagementservice.dto.TransactionRequestDTO;
import com.ihomziak.transactionmanagementservice.entity.Transaction;

public interface MapStructureMapper {

    Transaction mapToTransaction(TransactionRequestDTO transactionTMS);
}
