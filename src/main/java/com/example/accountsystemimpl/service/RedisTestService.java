package com.example.accountsystemimpl.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisTestService {

    private final RedissonClient redissonClient; // 이름 동일한 Bean 주입

    public String getLock() {

        RLock lock = redissonClient.getLock("sampleLock");

        try {
            boolean isLock = lock.tryLock(1, 3, TimeUnit.SECONDS);

            if(!isLock) {
                log.error("=====Lock acquisition failed =====");
                return "Lock failed";
            }
        }catch (Exception e) {
            log.error("Redis lock Failed");
        }

        return "Lock success";
    }
}
