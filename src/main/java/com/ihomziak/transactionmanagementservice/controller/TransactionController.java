package com.ihomziak.transactionmanagementservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ihomziak.transactionmanagementservice.dto.TransactionRequestDTO;
import com.ihomziak.transactionmanagementservice.producer.TransactionEventsProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionEventsProducer eventsProducer;

    @Autowired
    public TransactionController(TransactionEventsProducer eventsProducer) {
        this.eventsProducer = eventsProducer;
    }


    @PostMapping("/transaction")
    public ResponseEntity<TransactionRequestDTO> transfer(@RequestBody TransactionRequestDTO transactionDTO) throws JsonProcessingException {
        this.eventsProducer.sendFounds(transactionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionDTO);
    }
}
