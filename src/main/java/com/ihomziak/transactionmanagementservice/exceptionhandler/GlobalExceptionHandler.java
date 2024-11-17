package com.ihomziak.transactionmanagementservice.exceptionhandler;

import com.ihomziak.transactionmanagementservice.dto.ErrorDTO;
import com.ihomziak.transactionmanagementservice.exception.TransactionNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * Provides handling for exceptions throughout this service.
     *
     * @param ex      The target exception
     * @param request The current request
     */
    @ExceptionHandler({
            TransactionNotFoundException.class
    })
    @Nullable
    public final ResponseEntity<ErrorDTO> handleException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();

        log.error("Handling {} due to {}", ex.getClass().getSimpleName(), ex.getMessage());

        if (ex instanceof TransactionNotFoundException transactionNotFoundException) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            return handleException(transactionNotFoundException, headers, status, request);

        } else {
            if (log.isWarnEnabled()) {
                log.warn("Unknown exception type: {}", ex.getClass().getName());
            }

            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleExceptionInternal(ex, null, headers, status, request);
        }
    }

    /**
     * Customize the response for ClientNotFoundException.
     *
     * @param ex      The exception
     * @param headers The headers to be written to the response
     * @param status  The selected response status
     * @return a {@code ResponseEntity} instance
     */
    protected ResponseEntity<ErrorDTO> handleException(Exception ex,
                                                       HttpHeaders headers,
                                                       HttpStatus status,
                                                       WebRequest request) {
        return handleExceptionInternal(ex, new ErrorDTO(ex.getMessage()), headers, status, request);
    }

    /**
     * A single place to customize the response errorMessage of all Exception types.
     *
     * <p>The default implementation sets the {@link WebUtils#ERROR_EXCEPTION_ATTRIBUTE}
     * request attribute and creates a {@link ResponseEntity} from the given
     * errorMessage, headers, and status.
     *
     * @param ex           The exception
     * @param errorMessage The errorMessage for the response
     * @param headers      The headers for the response
     * @param status       The response status
     * @param request      The current request
     */
    protected ResponseEntity<ErrorDTO> handleExceptionInternal(Exception ex,
                                                               @Nullable ErrorDTO errorMessage,
                                                               HttpHeaders headers,
                                                               HttpStatus status,
                                                               WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }
        System.out.println(new ResponseEntity<>(errorMessage, headers, status));
        return new ResponseEntity<>(errorMessage, headers, status);
    }
}