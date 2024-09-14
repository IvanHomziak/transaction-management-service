package com.ihomziak.transactionmanagementservice.service.impl;

import com.ihomziak.transactionmanagementservice.dto.AccountRequestDTO;
import com.ihomziak.transactionmanagementservice.dto.AccountResponseDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class AccountServiceImpl {

    private final RestTemplate restTemplate;
    private final String accountServiceUrl = "http://localhost:8080/api/account";

    public AccountServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AccountRequestDTO getAccountByUuid(String clientUuid) {
        return restTemplate.getForObject(accountServiceUrl + "/" + clientUuid, AccountRequestDTO.class);
    }

    public void updateAccountByUuid(AccountRequestDTO object) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("X-HTTP-Method-Override", "PATCH");
        HttpEntity<AccountRequestDTO> entity = new HttpEntity<>(object, headers);

        restTemplate.exchange(accountServiceUrl, HttpMethod.PATCH, entity, AccountResponseDTO.class);
    }
}
