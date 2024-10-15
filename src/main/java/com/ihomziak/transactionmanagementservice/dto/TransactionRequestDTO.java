package com.ihomziak.transactionmanagementservice.dto;

import com.ihomziak.transactionmanagementservice.enums.TransactionStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequestDTO {

    private Integer transactionEventId;
    private String senderUuid;
    private String receiverUuid;
    private Double amount;
    private TransactionStatus transactionStatus;

    @Override
    public String toString() {
        return "TransactionRequestDTO{" +
                "transactionEventId=" + transactionEventId +
                ", senderUuid='" + senderUuid + '\'' +
                ", receiverUuid='" + receiverUuid + '\'' +
                ", amount=" + amount +
                ", transactionStatus=" + transactionStatus +
                '}';
    }
}
