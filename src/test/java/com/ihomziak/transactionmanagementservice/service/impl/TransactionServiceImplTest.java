package com.ihomziak.transactionmanagementservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihomziak.transactionmanagementservice.dao.CacheRepository;
import com.ihomziak.transactionmanagementservice.dao.TransactionRepository;
import com.ihomziak.transactionmanagementservice.dto.*;
import com.ihomziak.transactionmanagementservice.entity.Transaction;
import com.ihomziak.transactionmanagementservice.exception.TransactionNotFoundException;
import com.ihomziak.transactionmanagementservice.mapper.impl.MapStructureMapperImpl;
import com.ihomziak.transactionmanagementservice.producer.TransactionEventsProducer;
import com.ihomziak.transactioncommon.utils.TransactionStatus;
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
    void createTransaction_SaveToDatabase() throws JsonProcessingException {
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
    void getTransaction_Success() {
        when(cacheRepository.findTransactionByTransactionUuid("transaction-uuid")).thenReturn(transaction);

        Transaction result = transactionService.getTransaction("transaction-uuid");

        assertNotNull(result);
        assertEquals(transaction.getTransactionUuid(), result.getTransactionUuid());
        verify(cacheRepository, times(1)).findTransactionByTransactionUuid("transaction-uuid");
    }

    @Test
    void getTransaction_NotFound() {
        when(cacheRepository.findTransactionByTransactionUuid("transaction-uuid")).thenReturn(null);

        assertThrows(TransactionNotFoundException.class, () -> transactionService.getTransaction("transaction-uuid"));
        verify(cacheRepository, times(1)).findTransactionByTransactionUuid("transaction-uuid");
    }

    @Test
    void processTransaction_Success() throws JsonProcessingException {
        ConsumerRecord<Integer, String> consumerRecord = mock(ConsumerRecord.class);
        when(consumerRecord.value()).thenReturn("transaction-response");

        TransactionEventResponseDTO responseDTO = new TransactionEventResponseDTO();
        responseDTO.setTransactionUuid("transaction-uuid");
        responseDTO.setTransactionStatus(TransactionStatus.COMPLETED);
        when(objectMapper.readValue(anyString(), eq(TransactionEventResponseDTO.class))).thenReturn(responseDTO);

        when(cacheRepository.findTransactionByTransactionUuid("transaction-uuid")).thenReturn(transaction);
        when(transactionRepository.findTransactionByTransactionUuid("transaction-uuid")).thenReturn(Optional.of(transaction));

        transactionService.processTransaction(consumerRecord);

        verify(transactionRepository, times(1)).save(transaction);
        verify(cacheRepository, times(1)).save(transaction);
    }

    @Test
    void processTransaction_NotFoundInRedis() throws JsonProcessingException {
        ConsumerRecord<Integer, String> consumerRecord = mock(ConsumerRecord.class);
        when(consumerRecord.value()).thenReturn("transaction-response");

        TransactionEventResponseDTO responseDTO = new TransactionEventResponseDTO();
        responseDTO.setTransactionUuid("transaction-uuid");
        responseDTO.setTransactionStatus(TransactionStatus.FAILED);
        when(objectMapper.readValue(anyString(), eq(TransactionEventResponseDTO.class))).thenReturn(responseDTO);

        when(cacheRepository.findTransactionByTransactionUuid("transaction-uuid")).thenReturn(null);

        assertThrows(TransactionNotFoundException.class, () -> transactionService.processTransaction(consumerRecord));
        verify(cacheRepository, times(1)).findTransactionByTransactionUuid("transaction-uuid");
    }
}
