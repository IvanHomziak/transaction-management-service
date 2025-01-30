package com.ihomziak.bankingapp.transactionms.exceptionhandler;

import com.ihomziak.bankingapp.common.dto.ErrorDTO;
import com.ihomziak.bankingapp.transactionms.exception.TransactionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleTransactionNotFoundException_ShouldReturnNotFoundStatus() {
        // Arrange
        String errorMessage = "Transaction not found";
        TransactionNotFoundException exception = new TransactionNotFoundException(errorMessage);
        WebRequest request = mock(WebRequest.class);

        // Act
        ResponseEntity<ErrorDTO> response = globalExceptionHandler.handleException(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().getError());
    }

    @Test
    void handleUnknownException_ShouldReturnInternalServerError() {
        // Arrange
        String errorMessage = "An unexpected error occurred";
        Exception exception = new Exception(errorMessage);
        WebRequest request = mock(WebRequest.class);

        // Act
        ResponseEntity<ErrorDTO> response = globalExceptionHandler.handleException(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().getError());
    }

    @Test
    void handleExceptionInternal_ShouldReturnCorrectResponse() {
        // Arrange
        String errorMessage = "Internal error";
        Exception exception = new Exception(errorMessage);
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        WebRequest request = mock(WebRequest.class);

        // Act
        ResponseEntity<ErrorDTO> response = globalExceptionHandler.handleExceptionInternal(
                exception, new ErrorDTO(errorMessage), headers, status, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().getError());
    }
}
