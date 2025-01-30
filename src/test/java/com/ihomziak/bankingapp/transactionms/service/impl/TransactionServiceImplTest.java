package com.ihomziak.bankingapp.transactionms.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihomziak.bankingapp.common.utils.TransactionStatus;
import com.ihomziak.bankingapp.transactionms.dao.CacheRepository;
import com.ihomziak.bankingapp.transactionms.dao.TransactionRepository;
import com.ihomziak.bankingapp.transactionms.dto.TransactionEventResponseDTO;
import com.ihomziak.bankingapp.transactionms.dto.TransactionRequestDTO;
import com.ihomziak.bankingapp.transactionms.dto.TransactionStatusResponseDTO;
import com.ihomziak.bankingapp.transactionms.entity.Transaction;
import com.ihomziak.bankingapp.transactionms.exception.TransactionNotFoundException;
import com.ihomziak.bankingapp.transactionms.mapper.impl.MapStructureMapperImpl;
import com.ihomziak.bankingapp.transactionms.producer.TransactionEventsProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CacheRepository cacheRepository;

    @Mock
    private TransactionEventsProducer transactionEventsProducer;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MapStructureMapperImpl structureMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private TransactionRequestDTO transactionRequestDTO;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setSenderUuid("sender-uuid");
        transactionRequestDTO.setReceiverUuid("receiver-uuid");
        transactionRequestDTO.setAmount(100.0);
        transactionRequestDTO.setTransactionStatus(TransactionStatus.NEW);

        transaction = new Transaction();
        transaction.setTransactionUuid("transaction-uuid");
        transaction.setSenderUuid("sender-uuid");
        transaction.setReceiverUuid("receiver-uuid");
        transaction.setAmount(100.0);
        transaction.setTransactionStatus(TransactionStatus.NEW);
        transaction.setTransactionDate(LocalDateTime.now());
    }

    @Test
    void createTransaction_Success() throws JsonProcessingException {
        when(structureMapper.mapTransactionRequestDTOToTransaction(transactionRequestDTO)).thenReturn(transaction);
        when(objectMapper.writeValueAsString(any())).thenReturn("transaction-event-message");

        TransactionStatusResponseDTO responseDTO = new TransactionStatusResponseDTO();
        responseDTO.setTransactionUuid(transaction.getTransactionUuid());
        when(structureMapper.mapTransactionToTransactionStatusResponseDTO(transaction)).thenReturn(responseDTO);

        TransactionStatusResponseDTO result = transactionService.createTransaction(transactionRequestDTO);

        assertNotNull(result);
        assertEquals(transaction.getTransactionUuid(), result.getTransactionUuid());

        verify(cacheRepository, times(1)).save(transaction);
        verify(transactionEventsProducer, times(1)).sendTransactionMessage(anyInt(), anyString());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createTransaction_SaveTransaction() throws JsonProcessingException {
        transaction.setTransactionStatus(TransactionStatus.COMPLETED);

        when(structureMapper.mapTransactionRequestDTOToTransaction(transactionRequestDTO)).thenReturn(transaction);
        when(objectMapper.writeValueAsString(any())).thenReturn("transaction-event-message");

        TransactionStatusResponseDTO responseDTO = new TransactionStatusResponseDTO();
        responseDTO.setTransactionUuid(transaction.getTransactionUuid());
        when(structureMapper.mapTransactionToTransactionStatusResponseDTO(transaction)).thenReturn(responseDTO);

        TransactionStatusResponseDTO result = transactionService.createTransaction(transactionRequestDTO);

        assertNotNull(result);
        assertEquals(transaction.getTransactionUuid(), result.getTransactionUuid());

        verify(cacheRepository, times(1)).save(transaction);
        verify(transactionRepository, times(1)).save(transaction);
        verify(transactionEventsProducer, times(1)).sendTransactionMessage(anyInt(), anyString());
    }

    @Test
    void fetchTransaction_Success() {
        when(cacheRepository.findTransactionByTransactionUuid("transaction-uuid")).thenReturn(transaction);

        Transaction result = transactionService.fetchTransaction("transaction-uuid");

        assertNotNull(result);
        assertEquals(transaction.getTransactionUuid(), result.getTransactionUuid());
        verify(cacheRepository, times(1)).findTransactionByTransactionUuid("transaction-uuid");
    }

    @Test
    void fetchTransaction_NotFound() {
        when(cacheRepository.findTransactionByTransactionUuid("transaction-uuid")).thenReturn(null);

        assertThrows(TransactionNotFoundException.class, () -> transactionService.fetchTransaction("transaction-uuid"));
        verify(cacheRepository, times(1)).findTransactionByTransactionUuid("transaction-uuid");
    }

    @Test
    void processTransaction_EventResponse_Success() throws JsonProcessingException {
        ConsumerRecord<Integer, String> consumerRecord = mock(ConsumerRecord.class);
        when(consumerRecord.value()).thenReturn("transaction-response");

        TransactionEventResponseDTO responseDTO = new TransactionEventResponseDTO();
        responseDTO.setTransactionUuid("transaction-uuid");
        responseDTO.setTransactionStatus(TransactionStatus.COMPLETED);
        when(objectMapper.readValue(anyString(), eq(TransactionEventResponseDTO.class))).thenReturn(responseDTO);

        when(cacheRepository.findTransactionByTransactionUuid("transaction-uuid")).thenReturn(transaction);
        when(transactionRepository.findTransactionByTransactionUuid("transaction-uuid")).thenReturn(Optional.of(transaction));

        transactionService.processTransactionEventResponse(consumerRecord);

        verify(transactionRepository, times(1)).save(transaction);
        verify(cacheRepository, times(1)).save(transaction);
    }

    @Test
    void processTransaction_EventResponse_NotFoundInRedis() throws JsonProcessingException {
        ConsumerRecord<Integer, String> consumerRecord = mock(ConsumerRecord.class);
        when(consumerRecord.value()).thenReturn("transaction-response");

        TransactionEventResponseDTO responseDTO = new TransactionEventResponseDTO();
        responseDTO.setTransactionUuid("transaction-uuid");
        responseDTO.setTransactionStatus(TransactionStatus.FAILED);
        when(objectMapper.readValue(anyString(), eq(TransactionEventResponseDTO.class))).thenReturn(responseDTO);

        when(cacheRepository.findTransactionByTransactionUuid("transaction-uuid")).thenReturn(null);

        assertThrows(TransactionNotFoundException.class, () -> transactionService.processTransactionEventResponse(consumerRecord));
        verify(cacheRepository, times(1)).findTransactionByTransactionUuid("transaction-uuid");
    }
}
