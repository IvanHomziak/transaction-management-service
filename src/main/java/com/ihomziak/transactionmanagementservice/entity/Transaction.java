package com.ihomziak.transactionmanagementservice.entity;

import com.ihomziak.transactionmanagementservice.utils.TransactionStatus;
import com.ihomziak.transactionmanagementservice.utils.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private long transactionId;

    @Column(name = "transaction_uuid")
    private String transactionUuid;

    @Column(name = "sender_uuid")
    private String senderUuid;

    @Column(name = "receiver_uuid")
    private String receiverUuid;

    @Column(name = "amount")
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status")
    private TransactionStatus transactionStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionType transactionType;

    @CreatedDate
    @Column(name = "transaction_date", updatable = false)
    private LocalDateTime transactionDate;

    @LastModifiedDate
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;


}
