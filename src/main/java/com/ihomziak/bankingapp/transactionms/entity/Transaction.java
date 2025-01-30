package com.ihomziak.bankingapp.transactionms.entity;

import com.ihomziak.bankingapp.common.utils.TransactionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Objects;

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

    @CreatedDate
    @Column(name = "transaction_date", updatable = false)
    private LocalDateTime transactionDate;

    @LastModifiedDate
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return transactionId == that.transactionId && Double.compare(amount, that.amount) == 0 && Objects.equals(transactionUuid, that.transactionUuid) && Objects.equals(senderUuid, that.senderUuid) && Objects.equals(receiverUuid, that.receiverUuid) && transactionStatus == that.transactionStatus && Objects.equals(transactionDate, that.transactionDate) && Objects.equals(lastUpdate, that.lastUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, transactionUuid, senderUuid, receiverUuid, amount, transactionStatus, transactionDate, lastUpdate);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", transactionUuid='" + transactionUuid + '\'' +
                ", senderUuid='" + senderUuid + '\'' +
                ", receiverUuid='" + receiverUuid + '\'' +
                ", amount=" + amount +
                ", transactionStatus=" + transactionStatus +
                ", transactionDate=" + transactionDate +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
