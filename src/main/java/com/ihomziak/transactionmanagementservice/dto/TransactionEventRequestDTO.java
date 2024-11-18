package com.ihomziak.transactionmanagementservice.dto;

import com.ihomziak.transactioncommon.TransactionStatus;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionEventRequestDTO {
    private String transactionUuid;
    private String senderUuid;
    private String receiverUuid;
    private Double amount;
    private TransactionStatus transactionStatus;

    /**
     *         <dependency>
     *             <groupId>org.apache.httpcomponents</groupId>
     *             <artifactId>httpclient</artifactId>
     *             <version>4.5.13</version>
     *         </dependency>
     *         <dependency>
     *             <groupId>org.apache.httpcomponents.client5</groupId>
     *             <artifactId>httpclient5</artifactId>
     *             <version>5.3.1</version>
     *         </dependency>
     */
}