package com.ihomziak.transactionmanagementservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ihomziak.transactionmanagementservice.dto.TransactionRequestDTO;
import com.ihomziak.transactionmanagementservice.dto.TransactionStatusResponseDTO;
import com.ihomziak.transactionmanagementservice.entity.Transaction;
import com.ihomziak.transactionmanagementservice.service.impl.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionServiceImpl transactionService;

    @Autowired
    public TransactionController(TransactionServiceImpl transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transaction")
    public ResponseEntity<TransactionStatusResponseDTO> createTransaction(@RequestBody TransactionRequestDTO transactionDTO) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.transactionService.createTransaction(transactionDTO));
    }

    @GetMapping("/transaction/{uuid}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable String uuid) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(this.transactionService.getTransaction(uuid));
    }
}
