package com.ihomziak.transactionmanagementservice.scheduled;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ihomziak.transactioncommon.utils.TransactionStatus;
import com.ihomziak.transactionmanagementservice.entity.Transaction;
import com.ihomziak.transactionmanagementservice.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
public class TransactionScheduler {

    private final TransactionService transactionService;

    @Autowired
    public TransactionScheduler(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Scheduled(cron = "0 */2 * * * *", zone = "Europe/Warsaw")
    @Async
    @Transactional
    public void watchCreatedTransactions() {
        processTransactions(TransactionStatus.CREATED);
    }

    @Scheduled(cron = "0 */2 * * * *", zone = "Europe/Warsaw")
    @Async
    public void watchStartedTransactions() {
        processTransactions(TransactionStatus.STARTED);
    }

    private void processTransactions(TransactionStatus status) {
        log.info("Watching '{}'  transactions", status);
        List<Transaction> transactionsList = this.transactionService.fetchTransactionsByStatus(status);

        transactionsList.forEach(transaction -> {
            try {

                  log.info("Send transaction with status '{}'. Transaction: {}", status, transaction);
                  this.transactionService.sendTransactionEvent(transaction);

                  log.info("Transaction with status '{}' has been sent.", status);

                  if (status.equals(TransactionStatus.CREATED)) {
                      log.info("Update transaction with status 'CREATED'  to 'STARTED'");
                      transaction.setTransactionStatus(TransactionStatus.STARTED);
                      this.transactionService.saveToDatabase(transaction);
                      log.info("Transaction successfully updated. Status was changed to 'STARTED'");
                  }

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
