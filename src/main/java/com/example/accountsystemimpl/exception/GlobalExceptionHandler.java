package com.example.accountsystemimpl.exception;

import com.example.accountsystemimpl.type.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountException.class)
    public ResponseEntity<ErrorResponse> AccountExceptionHandler(
            AccountException exception
    ) {
        log.error("{} is occurred.", exception.getErrorCode());

        return ErrorResponse.toResponseEntity(exception.getErrorCode(), exception.getErrorMessage());
    }

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<ErrorResponse> TransactionExceptionHandler(
            TransactionException exception
    ) {
        log.error("{} is occurred.", exception.getErrorCode());

        return ErrorResponse.toResponseEntity(exception.getErrorCode(), exception.getErrorMessage());
    }
}
