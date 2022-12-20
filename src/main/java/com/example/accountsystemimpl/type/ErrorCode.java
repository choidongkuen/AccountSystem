package com.example.accountsystemimpl.type;


import lombok.AllArgsConstructor;
import lombok.Getter;

// CustomErrorClass -> ErrorCode errorCode + String message;
@Getter
@AllArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND("사용자가 없습니다."),
    MAX_COUNT_PER_USER_TEN("사용자 최대 계좌는 10개 입니다."),

    ACCOUNT_NOT_FOUND("해당 계좌가 존재하지 않습니다."),

    USER_ACCOUNT_UNMATCH("사용자 아이디와 계좌 소유주가 일치하지 않습니다."),

    ACCOUNT_ALREADY_STOP("계좌가 이미 해지되었습니다."),

    ACCOUNT_HAS_BALANCE("잔액이 있는 계좌는 해지할 수 없습니다."),

    ACCOUNT_ALREADY_UNREGISTERED("해당 계좌가 이미 해지 되었습니다."),


    AMOUNT_EXCEED_BALANCE("거래 금액이 잔액을 초과하였습니다.");


    private final String description;
}
