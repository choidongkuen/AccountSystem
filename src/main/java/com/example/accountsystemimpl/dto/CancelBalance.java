package com.example.accountsystemimpl.dto;

import com.example.accountsystemimpl.aop.AccountLockIdInterface;
import com.example.accountsystemimpl.type.TransactionResultType;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

public class CancelBalance {


    /** request 파라미터
     * "transactionId" : "abcerdsadsadwasdasd",
     * "accountNumber" : "1000000000",
     * "amount" : 1000
     */

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request implements AccountLockIdInterface {

        @NotBlank
        private String transactionId;

        @NotBlank
        @Size(min = 10, max = 10)
        private String accountNumber;

        @NotNull
        @Min(10)
        @Max(1000_000_000)
        private Long amount;

    }

    /** response 파라미터
     * "accountNumber" : "1000000000",
     * "transactionResult" : "S",
     * "transactionId" : "asdasdsadwe21312asd"
     * "amount" : 1000 ,
     * "transactionAt" ~~~
     */

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {

        private String accountNumber;
        private TransactionResultType transactionResultType;
        private Long amount;
        private String transactionId;
        private LocalDateTime transactionAt;

        public static CancelBalance.Response fromTransactionDto(TransactionDto dto) {

            return CancelBalance.Response.builder()
                                  .accountNumber(dto.getAccountNumber())
                                  .transactionResultType(dto.getTransactionResultType())
                                  .amount(dto.getAmount())
                                  .transactionId(dto.getTransactionId())
                                  .transactionAt(dto.getTransactionAt())
                                  .build();
        }

    }
}