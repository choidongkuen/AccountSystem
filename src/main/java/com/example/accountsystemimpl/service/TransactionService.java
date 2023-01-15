package com.example.accountsystemimpl.service;


import com.example.accountsystemimpl.domain.Account;
import com.example.accountsystemimpl.domain.AccountUser;
import com.example.accountsystemimpl.domain.Transaction;
import com.example.accountsystemimpl.dto.TransactionDto;
import com.example.accountsystemimpl.exception.AccountException;
import com.example.accountsystemimpl.repository.AccountRespository;
import com.example.accountsystemimpl.repository.AccountUserRepository;
import com.example.accountsystemimpl.repository.TransactionRepository;
import com.example.accountsystemimpl.type.ErrorCode;
import com.example.accountsystemimpl.type.TransactionResultType;
import com.example.accountsystemimpl.type.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
@Service
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


    // 잔액 사용
    @Transactional
    public TransactionDto useBalance(Long userId, String accountNumber, Long amount) {

        AccountUser accountUser = accountUserRepository.findById(userId)
                                                       .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

        Account account = accountRespository.findByAccountNumber(accountNumber)
                                            .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validationUseBalance(accountUser, account, amount);

        account.useBalance(amount); // Account 메소드 호출

        return TransactionDto.fromEntity(getTransactionDto(USE, SUCCESS, account, amount));

    }

    private void validationUseBalance(AccountUser accountUser, Account account, Long amount) {

        if (!Objects.equals(account.getAccountUser().getId(), accountUser.getId())) {
            throw new AccountException(ErrorCode.USER_ACCOUNT_UNMATCH);
        }

        if (!Objects.equals(account.getAccountStatus(), IN_USE)) {
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }

    }

//    @Transactional
//    public void saveFailedUserTransaction(String accountNumber, Long amount) {
//
//        Account account = accountRespository.findByAccountNumber(accountNumber)
//                                            .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
//
//        getTransactionDto(USE, FAIL, account, amount);
//    }


    // Option + Command + M
    // 거래 내역 저장
    private Transaction getTransactionDto(
            TransactionType transactionType,
            TransactionResultType transactionResultType,
            Account account,
            Long amount
    ) {
        return (transactionRepository.save(Transaction.builder()
              .transactionType(transactionType)
              .transactionResultType(transactionResultType)
              .account(account)
              .amount(amount)
              .balanceSnapshot(account.getBalance())
              .transactionId(UUID.randomUUID().toString().replace("-", ""))
              .transactionAt(LocalDateTime.now())
              .build()));
    }



    /**
     * 1. 거래 아이디에 해당하는 거래가 없는 경우
     * 2. 계좌가 없는 경우
     * 3. 거래와 계좌가 일치하지 않는 경우
     * 4. 거래 금액과 거래 취소 금액이 다른 경우
     * 5. 1년이 넘은 거래는 사용 취소 불가능
     * 6. 해당 계좌에서 거래가 진행 중일 때 불가능
     */

    @Transactional
    public TransactionDto cancelBalance(
            String transactionId,
            String accountNumber,
            Long amount
    ) {

        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                                   .orElseThrow(() -> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND)
        );

        Account account = accountRespository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND)
        );

        validationCancelBalance(transaction,account,amount);

        account.cancelBalance(amount);

        return TransactionDto.fromEntity(
               transactionRepository.save(Transaction.builder()
                       .transactionType(CANCEL)
                       .transactionResultType(SUCCESS)
                       .account(account)
                       .amount(amount)
                       .balanceSnapshot(account.getBalance())
                       .transactionId(UUID.randomUUID().toString())
                       .transactionAt(LocalDateTime.now())
                       .build())
        );
    }


    private void validationCancelBalance(Transaction transaction, Account account, Long amount) {

        // 계좌가 다른 경우
        if(transaction.getAccount() != account) {
            throw new AccountException(ErrorCode.TRANSACTION_ACCOUNT_UNMATCH);
        }

        // 취소 금액과 사용 금액이 다른 경우
        if(!Objects.equals(transaction.getAmount(), amount)){
            throw new AccountException(ErrorCode.TRANSACTIONAMOUNT_CANCELAMOUNT_UNMATCH);
        }

        // 1년 지난 거래인 경우
        if(transaction.getTransactionAt().isBefore(LocalDateTime.now().minusYears(1))) {
            throw new AccountException(ErrorCode.CANCEL_AFTER_ONE_YEAR_TRANSACTION);
        }
    }


//    /**
//     * 1. 거래 아이디에 해당하는 거래가 없는 경우
//     * 2. 계좌가 없는 경우
//     * 3. 거래와 계좌가 일치하지 않는 경우
//     * 4. 거래 금액과 거래 취소 금액이 다른 경우
//     * 5. 1년이 넘은 거래는 사용 취소 불가능
//     * 6. 해당 계좌에서 거래가 진행 중일 때 불가능
//     */
//
//    @Transactional
//    public TransactionDto cancelBalance(String transactionId,
//                                        String accountNumber,
//                                        Long amount) {
//
//        // 거래 아이디에 해당하는 거래가 없는 경우
//        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
//                                                       .orElseThrow(() -> new TransactionException(ErrorCode.TRANSACTION_NOT_FOUND));
//        // 계좌가 존재하지 않는 경우
//        Account account = accountRespository.findByAccountNumber(accountNumber)
//                                            .orElseThrow(() -> new TransactionException(ErrorCode.ACCOUNT_NOT_FOUND));
//
//
//        validationCancelBalance(transaction, account, amount);
//
//        account.cancelBalance(amount); // 거래 내역 취소
//
//        return TransactionDto.fromEntity(getTransactionDto(
//                CANCEL,
//                SUCCESS,
//                account,
//                amount
//        ));
//    }
//
//    private void validationCancelBalance(Transaction transaction,
//                                         Account account,
//                                         Long amount) {
//
//        // 거래와 계좌가 일치하지 않는 경우
//        if (account.getId() != transaction.getAccount().getId()) {
//            throw new TransactionException(ErrorCode.TRANSACTION_ACCOUNT_UNMATCH);
//        }
//
//        // 거래 금액과 거래 취소 금액이 다른 경우
//        if (!Objects.equals(transaction.getAmount(), amount)) {
//            throw new TransactionException(ErrorCode.TRANSACTIONAMOUNT_CANCELAMOUNT_UNMATCH);
//        }
//        // 1년이 넘은 거래는 사용 취소 불가능
//        if (transaction.getTransactionAt().isBefore(LocalDateTime.now().minusYears(1))) {
//            throw new TransactionException(ErrorCode.CANCEL_AFTER_ONE_YEAR_TRANSACTION);
//        }
//
//    }
//

    @Transactional
    public void saveFailedCancelTransaction(String accountNumber, Long amount){

        Account account = accountRespository.findByAccountNumber(accountNumber)
                                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        getTransactionDto(CANCEL, FAIL, account, amount);

    }


}

//}
}
