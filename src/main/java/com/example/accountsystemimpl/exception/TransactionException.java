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

public class TransactionException extends RuntimeException{

    public ErrorCode errorCode;
    public String errorMessage;

    public TransactionException(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
