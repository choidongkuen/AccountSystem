package com.example.accountsystemimpl.service;

import com.example.accountsystemimpl.domain.Account;
import com.example.accountsystemimpl.domain.AccountUser;
import com.example.accountsystemimpl.dto.AccountDto;
import com.example.accountsystemimpl.dto.AccountInfo;
import com.example.accountsystemimpl.exception.AccountException;
import com.example.accountsystemimpl.repository.AccountRespository;
import com.example.accountsystemimpl.repository.AccountUserRepository;
import com.example.accountsystemimpl.type.AccountStatus;
import com.example.accountsystemimpl.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.accountsystemimpl.type.AccountStatus.IN_USE;
import static com.example.accountsystemimpl.type.ErrorCode.*;
import static com.example.accountsystemimpl.type.ErrorCode.USER_NOT_FOUND;


@Slf4j
@Service
@RequiredArgsConstructor

public class AccountService {

    private final AccountRespository accountRespository;
    private final AccountUserRepository accountUserRepository;

    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance) {

        // 사용자가 있는지 조회
        // 계좌의 번호 생성(저장된 최신 계좌번호 + 1)
        // 계좌를 저장하고, 정보 저장
        AccountUser accountUser = accountUserRepository.findById(userId).orElseThrow(() ->
                new AccountException(USER_NOT_FOUND));

        validateCreateAccount(accountUser);

        String newAccountNumber = accountRespository.findFirstByOrderByIdDesc()
                       .map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "")
                       .orElse("1000000000");


        // Service -> Controller ( Entity -> Dto)
        // 1회성 변수 사용 주의!
        return AccountDto.fromEntity(accountRespository.save(Account.builder()
                                                                .accountUser(accountUser)
                                                                .accountNumber(newAccountNumber)
                                                                .accountStatus(IN_USE)
                                                                .balance(initialBalance)
                                                                .registeredAt(LocalDateTime.now())
                                                                .build()));
    }

    // 가독성을 위해 메소드 추출(Option + Command + M)
    private void validateCreateAccount(AccountUser accountUser) {
        if(accountRespository.countByAccountUser(accountUser) >= 10){
            throw new AccountException(MAX_COUNT_PER_USER_TEN);
        }
    }

    @Transactional
    public Account getAccount(Long id){
        return accountRespository.findById(id).get();
    }

    @Transactional
    public AccountDto deleteAccount(Long userId, String accountNumber) {

        // 사용자 없는 경우
        AccountUser accountUser = accountUserRepository.findById(userId)
                                .orElseThrow(() -> new AccountException(USER_NOT_FOUND)
        );

        // 계좌가 없는 경우
        Account account = accountRespository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND)
        );

        // 계좌 소유주과 사용자 아이디가 다른 경우
        // 계좌가 이미 해지 상태인 경우
        // 계좌 잔액이 남아있는 경우
        validateDeleteAccount(accountUser, account);

        account.setUnRegisteredAt(LocalDateTime.now());
        account.setAccountStatus(AccountStatus.STOP_USE);
        accountRespository.save(account);

        return AccountDto.fromEntity(account);
    }
    private static void validateDeleteAccount(AccountUser accountUser, Account account) {

        // 계좌 소유주가 다른 경우
        if(accountUser.getId() != account.getAccountUser().getId()){
            throw new AccountException(USER_ACCOUNT_UNMATCH);
        }

        // 계좌가 이미 해지 상태인 경우
        if(account.getAccountStatus() == AccountStatus.STOP_USE){
            throw new AccountException(ACCOUNT_ALREADY_STOP);
        }

        // 계좌 잔액이 남아있는 경우
        if(account.getBalance() > 0){
            throw new AccountException(ACCOUNT_HAS_BALANCE);
        }
    }

    @Transactional
    public List<AccountDto> getAccountsByUserId(Long userId) {

        AccountUser user = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));


        List<Account> accounts = accountRespository.findByAccountUser(user);


        return accounts.stream()
                .map(AccountDto::fromEntity)
                .collect(Collectors.toList());

    }
}