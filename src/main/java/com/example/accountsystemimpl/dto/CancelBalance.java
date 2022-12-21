package com.example.accountsystemimpl.dto;


import com.example.accountsystemimpl.type.TransactionResultType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.*;
import java.time.LocalDateTime;


/**
 *  {
 *      "transactionId",
 *      "accountNumber",
 *      "amount"
 *  }
 */
public class CancelBalance {


    @Slf4j
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class Request{


        @NotNull
        private String transactionId;

        @NotBlank
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

    public static class Response{

        private String accountNumber;

        private TransactionResultType transactionResultType;

        private String transactionId;

        private Long amount;

        private LocalDateTime transactedAt;

        public static Response fromTransactionDto(TransactionDto dto){

            return Response.builder()
                    .transactionId(dto.getTransactionId())
                    .accountNumber(dto.getAccountNumber())
                    .transactionResultType(dto.getTransactionResultType())
                    .amount(dto.getAmount())
                    .transactedAt(dto.getTransactionAt())
                    .build();
        }

    }

}
