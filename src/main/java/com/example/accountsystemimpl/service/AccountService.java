package com.example.accountsystem.service;

import com.example.accountsystem.domain.Account;
import com.example.accountsystem.domain.AccountUser;
import com.example.accountsystem.dto.AccountDto;
import com.example.accountsystem.exception.AccountException;
import com.example.accountsystem.repository.AccountRespository;
import com.example.accountsystem.repository.AccountUerRepository;
import com.example.accountsystem.type.AccountStatus;
import com.example.accountsystem.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.accountsystem.type.AccountStatus.IN_USE;


@Slf4j
@Service
@RequiredArgsConstructor

public class AccountService {

    private final AccountRespository accountRespository;
    private final AccountUerRepository accountUerRepository;

    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance) {

        // 사용자가 있는지 조회
        // 계좌의 번호 생성(저장된 최신 계좌번호 + 1)
        // 계좌를 저장하고, 정보 저장
        AccountUser accountUser = accountUerRepository.findById(userId).orElseThrow(() ->
                new AccountException(ErrorCode.USER_NOT_FOUND));

        String newAccoutNumber = accountRespository.findLastByOrderById()
                       .map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "")
                       .orElse("1000000000");


        // Service -> Controller (Dto)
        // 1회성 변수 사용 주의!
        return AccountDto.fromEntity(accountRespository.save(Account.builder()
                                                  .accountUser(accountUser)
                                                  .accountNumber(newAccoutNumber)
                                                  .accountStatus(IN_USE)
                                                  .balance(initialBalance)
                                                  .registeredAt(LocalDateTime.now())
                                                  .build()));


    }

}