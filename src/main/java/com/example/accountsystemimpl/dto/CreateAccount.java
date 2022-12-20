package com.example.accountsystem.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


public class CreateAccount {

    @Getter
    @Setter
    @Builder
    @ToString
    public static class Request {

        @NotNull
        @Min(1)
        private Long userId;


        @NotNull
        @Min(100)
        private Long initialBalance;

    }

    @Getter
    @Setter
    @Builder
    @ToString
    public static class Response {

        private Long userId;
        private String accountNumber;
        private LocalDateTime registerAt;

        public static Response FromAccountDto(AccountDto accountDto) {

            return Response.builder()
                       .userId(accountDto.getUserId())
                       .accountNumber(accountDto.getAccountNumber())
                       .registerAt(accountDto.getRegisteredAt())
                       .build();
        }
    }

}
