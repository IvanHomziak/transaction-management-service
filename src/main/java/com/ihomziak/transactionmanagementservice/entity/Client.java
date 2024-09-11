package com.ihomziak.transactionmanagementservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "client")
@Getter
@Setter
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private long clientId;

    @Column(name = "uuid")
    private String UUID;

    @OneToMany(mappedBy = "client")
    private List<Transaction> transactionList;
}
