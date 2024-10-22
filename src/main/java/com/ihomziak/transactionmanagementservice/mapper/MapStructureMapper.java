package com.ihomziak.transactionmanagementservice.mapper;

import com.ihomziak.transactionmanagementservice.dto.TransactionResponseDTO;
import com.ihomziak.transactionmanagementservice.entity.Transaction;

public interface MapStructureMapper {

    TransactionResponseDTO mapTransactionResponseDTOFromTransaction(Transaction transaction );
}
