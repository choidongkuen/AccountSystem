package com.example.accountsystemimpl.dto;

import com.example.accountsystemimpl.type.TransactionResultType;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

public class CancelBalance {

    /**
     * request 파라미터
     * {
     * "transactionId" : "1" ,
     * "accountNumber" : "1000000000",
     * "amount" : 1000
     * }
     */

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request {

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

    /**
     * response 파라미터
     * {
     * "accountNumber" : "1000000000",
     * "transactionRequest" : "S",
     * "transactionId" ~~~~,
     * "amount" : 1000 ,
     * "transactionAt" : ~~~~
     * }
     */

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private String accountNumber;

        private TransactionResultType transactionResultType;

        private String transactionId;

        private Long amount;

        private LocalDateTime transactionAt;

        public static Response from(TransactionDto transactionDto) {

            return Response.builder()
                           .accountNumber(transactionDto.getAccountNumber())
                           .transactionResultType(transactionDto.getTransactionResultType())
                           .transactionId(transactionDto.getTransactionId())
                           .amount(transactionDto.getAmount())
                           .transactionAt(transactionDto.getTransactionAt())
                           .build();
        }
    }
}
