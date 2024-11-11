package com.ihomziak.transactionmanagementservice.dao.impl;

import com.ihomziak.transactionmanagementservice.dao.TransactionCacheRepository;
import com.ihomziak.transactionmanagementservice.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TransactionCacheRepositoryImpl implements TransactionCacheRepository {

    private final RedisUtil<String> redisStringUtil;

    @Autowired
    public TransactionCacheRepositoryImpl(RedisUtil<String> redisStringUtil) {
        this.redisStringUtil = redisStringUtil;
    }

    @Override
    public void saveTransaction(String uuid, String object) {
        redisStringUtil.putValue(uuid, object);
        redisStringUtil.setExpire(uuid, 10, TimeUnit.MINUTES);
    }

    @Override
    public String findTransactionByIdKey(String key) {
        return redisStringUtil.getValue(key);
    }
}