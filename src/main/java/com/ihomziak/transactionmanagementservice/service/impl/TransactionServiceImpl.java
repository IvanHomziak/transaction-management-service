package com.ihomziak.transactionmanagementservice.service.impl;

import com.ihomziak.transactionmanagementservice.dao.TransRepository;
import com.ihomziak.transactionmanagementservice.dto.AccountRequestDTO;
import com.ihomziak.transactionmanagementservice.dto.TransactionRequestDTO;
import com.ihomziak.transactionmanagementservice.dto.TransactionResponseDTO;
import com.ihomziak.transactionmanagementservice.entity.Client;
import com.ihomziak.transactionmanagementservice.entity.Transaction;
import com.ihomziak.transactionmanagementservice.exception.BalanceException;
import com.ihomziak.transactionmanagementservice.exception.ClientNotFoundException;
import com.ihomziak.transactionmanagementservice.mapper.MapStructureMapper;
import com.ihomziak.transactionmanagementservice.service.TransactionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final AccountServiceImpl accountServiceImpl;
    private final TransRepository transRepository;
    private final MapStructureMapper mapper;

    public TransactionServiceImpl(AccountServiceImpl accountServiceImpl, TransRepository transRepository, MapStructureMapper mapper) {
        this.accountServiceImpl = accountServiceImpl;
        this.transRepository = transRepository;
        this.mapper = mapper;
    }

    @Override
    public TransactionResponseDTO processTransaction(TransactionRequestDTO transactionRequestDTO) {

        AccountRequestDTO sender = this.accountServiceImpl.getAccountByUuid(transactionRequestDTO.getSenderUuid());

        if (sender == null) {
            throw new ClientNotFoundException("Client not found, UUID: " + transactionRequestDTO.getSenderUuid());
        }

        if (sender.getBalance() < transactionRequestDTO.getAmount()) {
            throw new BalanceException("Insufficient balance");
        }
        AccountRequestDTO receiver = this.accountServiceImpl.getAccountByUuid(transactionRequestDTO.getReceiverUuid());

        sender.setBalance(sender.getBalance() - transactionRequestDTO.getAmount());
        this.accountServiceImpl.updateAccountByUuid(sender);

        receiver.setBalance(receiver.getBalance() + transactionRequestDTO.getAmount());
        this.accountServiceImpl.updateAccountByUuid(receiver);

        Transaction senderTransaction = new Transaction();
        senderTransaction.setAmount(sender.getBalance());
        senderTransaction.setAccountNumber(senderTransaction.getAccountNumber());
        senderTransaction.setTransactionDate(LocalDateTime.now());

        Transaction receiverTransaction = new Transaction();
        receiverTransaction.setAmount(receiver.getBalance());
        receiverTransaction.setAccountNumber(receiverTransaction.getAccountNumber());
        receiverTransaction.setTransactionDate(LocalDateTime.now());

        Client senderClient = new Client();
        senderClient.setUUID(sender.getClientUUID());
        senderClient.setTransactionList(List.of(senderTransaction));

        Client receiverClient = new Client();
        receiverClient.setUUID(sender.getClientUUID());
        receiverClient.setTransactionList(List.of(senderTransaction));

        senderTransaction.setClient(senderClient);
        senderTransaction.setClient(receiverClient);

        this.transRepository.save(senderTransaction);
        this.transRepository.save(receiverTransaction);
        return this.mapper.accountResponseDtoToTransactionResponseDto(sender, receiver);
    }
}
