package com.ihomziak.transactionmanagementservice.mapper;

import com.ihomziak.transactionmanagementservice.dto.AccountRequestDTO;
import com.ihomziak.transactionmanagementservice.dto.TransactionResponseDTO;

public interface MapStructureMapper {

    TransactionResponseDTO accountResponseDtoToTransactionResponseDto(AccountRequestDTO sender, AccountRequestDTO receiver);
}
