package com.ihomziak.transactionmanagementservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ihomziak.transactionmanagementservice.dto.TransactionRequestDTO;
import com.ihomziak.transactionmanagementservice.dto.TransactionStatusResponseDTO;
import com.ihomziak.transactionmanagementservice.entity.Transaction;
import com.ihomziak.transactionmanagementservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transaction")
    public ResponseEntity<TransactionStatusResponseDTO> createTransaction(@RequestBody TransactionRequestDTO transactionDTO) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.transactionService.createTransaction(transactionDTO));
    }

    @GetMapping("/transaction/{uuid}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable String uuid) {
        return ResponseEntity.status(HttpStatus.OK).body(this.transactionService.fetchTransaction(uuid));
    }

    @GetMapping("/transaction/cancel/{uuid}")
    public ResponseEntity<String>  canselTransaction(@PathVariable String uuid) {
        this.transactionService.cancelTransaction(uuid);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Transaction CANCELED");
    }
}
