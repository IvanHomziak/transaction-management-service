package com.ihomziak.transactionmanagementservice.service;

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihomziak.transactionmanagementservice.entity.Transaction;
import com.ihomziak.transactionmanagementservice.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private final RedisUtil<String> redisStringUtil;
    private final ObjectMapper objectMapper;

    @Autowired
    public RedisService(RedisUtil<String> redisStringUtil, ObjectMapper objectMapper) {
        this.redisStringUtil = redisStringUtil;
        this.objectMapper = objectMapper;
    }


    public void addRedis(Transaction request) throws JsonProcessingException {
        String object = objectMapper.writeValueAsString(request);
        redisStringUtil.putValue(request.getTransactionUuid(), object);
        redisStringUtil.setExpire(request.getTransactionUuid(), 10, TimeUnit.MINUTES);
    }

    public String getValue(String key) {

        return redisStringUtil.getValue(key);
    }
}
