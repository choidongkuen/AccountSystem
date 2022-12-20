package com.example.accountsystemimpl.service;


import com.example.accountsystemimpl.domain.Account;
import com.example.accountsystemimpl.domain.AccountUser;
import com.example.accountsystemimpl.domain.Transaction;
import com.example.accountsystemimpl.dto.TransactionDto;
import com.example.accountsystemimpl.dto.UseBalance;
import com.example.accountsystemimpl.exception.AccountException;
import com.example.accountsystemimpl.repository.AccountRespository;
import com.example.accountsystemimpl.repository.AccountUserRepository;
import com.example.accountsystemimpl.repository.TransactionRepository;
import com.example.accountsystemimpl.type.ErrorCode;
import com.example.accountsystemimpl.type.TransactionResultType;
import com.example.accountsystemimpl.type.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.example.accountsystemimpl.type.AccountStatus.IN_USE;
import static com.example.accountsystemimpl.type.TransactionResultType.FAIL;
import static com.example.accountsystemimpl.type.TransactionResultType.SUCCESS;
import static com.example.accountsystemimpl.type.TransactionType.CANCEL;
import static com.example.accountsystemimpl.type.TransactionType.USE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {


    /**
     * 1. 사용자가 없는 경우
     * 2. 사용자 아이디와 계좌 소유자가 다른 경우
     * 3. 걔좌가 이미 해지 상태인 경우
     * 4. 거래 금액이 잔액보다 큰 경우
     * 5. 거래 금액이 너무 작거나 큰 경우
     */

    private final TransactionRepository transactionRepository;
    private final AccountUserRepository accountUserRepository;
    private final AccountRespository accountRespository;


    @Transactional
    public TransactionDto useBalance(UseBalance.Request request) {

        AccountUser accountUser = accountUserRepository.findById(request.getUserId())
                                                       .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

        Account account = accountRespository.findByAccountNumber(request.getAccountNumber())
                                            .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validationUseBalance(accountUser, account, request.getAmount());

        account.useBalance(request.getAmount()); // Account 메소드 호출

        return getTransactionDto(SUCCESS, account, request);

    }

    private void validationUseBalance(AccountUser accountUser, Account account, Long amount) {

        if (account.getAccountUser().getId() != accountUser.getId()) {
            throw new AccountException(ErrorCode.USER_ACCOUNT_UNMATCH);
        }

        if (!Objects.equals(account.getAccountStatus(), IN_USE)) {
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }

    }

    @Transactional
    public TransactionDto saveFailedTransaction(UseBalance.Request request) {

        Account account = accountRespository.findById(request.getUserId())
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

        return getTransactionDto(FAIL, account, request);

    }


    // Option + Command + M
    private TransactionDto getTransactionDto(TransactionResultType transactionResultType,
                                     Account account, UseBalance.Request request
    ) {
        return TransactionDto.fromEntity(transactionRepository.save(Transaction.builder()
                               .transactionType(USE)
                               .transactionResultType(transactionResultType)
                               .account(account)
                               .amount(request.getAmount())
                               .balanceSnapshot(account.getBalance())
                               .transactionId(UUID.randomUUID().toString().replace("-", ""))
                               .transactionAt(LocalDateTime.now())
                               .build()));
    }
}
