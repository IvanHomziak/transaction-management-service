package com.ihomziak.transactionmanagementservice.service.impl;

import com.ihomziak.transactionmanagementservice.dto.AccountRequestDTO;
import com.ihomziak.transactionmanagementservice.dto.AccountResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AccountServiceImpl {

    private final RestTemplate restTemplate;
    private final String accountServiceUrl = "http://localhost:8080/api/account/";

    public AccountServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AccountRequestDTO getAccountByUuid(String clientUuid) {
        return restTemplate.getForObject(accountServiceUrl + clientUuid, AccountRequestDTO.class);
    }

    public AccountResponseDTO updateAccountByUuid(AccountRequestDTO clientUuid) {
        return restTemplate.patchForObject(accountServiceUrl + clientUuid.getClientUUID(), AccountRequestDTO.class, AccountResponseDTO.class);
    }
}
