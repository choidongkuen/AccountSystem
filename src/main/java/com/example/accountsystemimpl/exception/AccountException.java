package com.example.accountsystemimpl.exception;


import com.example.accountsystemimpl.type.ErrorCode;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter

public class AccountException extends RuntimeException{

    private ErrorCode errorCode;
    private String errorMessage;


    public AccountException(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
