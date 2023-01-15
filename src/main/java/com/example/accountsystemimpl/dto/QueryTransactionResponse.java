package com.example.accountsystemimpl.dto;


import com.example.accountsystemimpl.type.TransactionResultType;
import com.example.accountsystemimpl.type.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class QueryTransactionResponse {

    private String accountNumber;

    private TransactionType transactionType;

    private TransactionResultType transactionResultType;

    private String transactionId;

    private Long amount;

    private LocalDateTime transactionAt;

    public static QueryTransactionResponse fromTransactionDto(TransactionDto dto) {


        return QueryTransactionResponse.builder()
                .accountNumber(dto.getAccountNumber())
                .transactionType(dto.getTransactionType())
                .transactionResultType(dto.getTransactionResultType())
                .transactionId(dto.getTransactionId())
                .amount(dto.getAmount())
                .transactionAt(dto.getTransactionAt())
                .build();

    }
}
