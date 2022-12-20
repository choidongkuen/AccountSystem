package com.example.accountsystemimpl.dto;


import com.example.accountsystemimpl.domain.Transaction;
import com.example.accountsystemimpl.type.TransactionType;
import com.example.accountsystemimpl.type.TransactionResultType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TransactionDto {


    private TransactionType transactionType;

    private TransactionResultType transactionResultType;

    private String accountNumber; // Account 모든 정보가 필요 없기 때문 ( Dto 의 장점 : 원하는 정보만 )

    private Long amount;

    private Long balanceSnapshot;

    private String transactionId;

    private LocalDateTime transactionAt;

    public static TransactionDto fromEntity(Transaction transaction) {

        return TransactionDto.builder()
                             .transactionType(transaction.getTransactionType())
                             .transactionResultType(transaction.getTransactionResultType())
                             .accountNumber(transaction.getAccount().getAccountNumber())
                             .amount(transaction.getAmount())
                             .balanceSnapshot(transaction.getBalanceSnapshot())
                             .transactionId(transaction.getTransactionId())
                             .transactionAt(transaction.getTransactionAt())
                             .build();
    }
}
