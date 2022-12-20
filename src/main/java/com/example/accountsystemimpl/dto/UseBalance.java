package com.example.accountsystemimpl.dto;

import com.example.accountsystemimpl.type.TransactionResultType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class UseBalance {


    @Slf4j
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class Request{

        @NotNull
        @Min(1)
        private Long userId;

        @NotNull
        @Size(min = 10, max = 10)
        private String accountNumber;

        @NotNull
        @Min(10)
        @Max(1000_000_000)
        private Long amount;
    }

    @Slf4j
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class Response {

        private String accountNumber;
        private TransactionResultType transactionResult;
        private Long amount;
        private String transactionId;
        private LocalDateTime transactionAt;

        public static Response fromAccountDto(TransactionDto dto) {

            return Response.builder()
                    .accountNumber(dto.getAccountNumber())
                    .transactionResult(dto.getTransactionResultType())
                    .amount(dto.getAmount())
                    .transactionId(dto.getTransactionId())
                    .transactionAt(dto.getTransactionAt())
                    .build();
        }
    }
}
