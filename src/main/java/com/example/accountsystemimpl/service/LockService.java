package com.example.accountsystemimpl.service;


import com.example.accountsystemimpl.exception.AccountException;
import com.example.accountsystemimpl.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class LockService {

    private final RedissonClient redissonClient; // 이름 동일한 Bean 주입

    public String lock(String accountNumber) {

        RLock lock = redissonClient.getLock(getLockKey(accountNumber));
        log.debug("Trying lock for AccountNumber : {}", accountNumber);

        try {
            boolean isLock = lock.tryLock(1, 5, TimeUnit.SECONDS);
            // waitTime : 기다리는 시간
            // leaseTime : 자동으로 풀리는 시간

            if(!isLock) {
                log.error("=====Lock acquisition failed=====");
                throw new AccountException(ErrorCode.ACCOUNT_TRANSACTION_LOCK);
            }
        }
        catch(AccountException e) {
            throw e;
        }catch(Exception e) {
            log.error("Redis lock Failed");
        }
        return "Lock success";
    }

    public void unlock(String accountNumber) {
        log.debug("Unlock for accountNumber : {}",accountNumber);
        redissonClient.getLock(getLockKey(accountNumber)).unlock();
    }

    private String getLockKey(String accountNumber) {
        return "ACLK:" + accountNumber;
    }
}
