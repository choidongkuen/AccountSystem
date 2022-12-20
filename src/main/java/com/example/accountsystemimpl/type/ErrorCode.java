package com.example.accountsystem.type;


import lombok.AllArgsConstructor;
import lombok.Getter;

// CustomErrorClass -> ErrorCode errorCode + String message;
@Getter
@AllArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND("사용자가 없습니다.");

    private final String description;
}
