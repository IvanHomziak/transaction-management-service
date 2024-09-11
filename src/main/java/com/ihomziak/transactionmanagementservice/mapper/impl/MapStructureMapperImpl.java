package com.ihomziak.transactionmanagementservice.mapper.impl;

import com.ihomziak.transactionmanagementservice.dto.AccountRequestDTO;
import com.ihomziak.transactionmanagementservice.dto.TransactionResponseDTO;
import com.ihomziak.transactionmanagementservice.mapper.MapStructureMapper;
import org.springframework.stereotype.Component;

@Component
public class MapStructureMapperImpl implements MapStructureMapper {

    @Override
    public TransactionResponseDTO accountResponseDtoToTransactionResponseDto(AccountRequestDTO sender, AccountRequestDTO receiver) {
        TransactionResponseDTO responseDto = new TransactionResponseDTO();
        responseDto.setSenderUuid(sender.getClientUUID());
        responseDto.setSenderBalance(sender.getBalance());
        responseDto.setRecipientUuid(receiver.getClientUUID());
        responseDto.setRecipientBalance(receiver.getBalance());
        return responseDto;
    }
}
