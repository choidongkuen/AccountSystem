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

    AMOUNT_EXCEED_BALANCE("거래 금액이 잔액을 초과하였습니다."),

    TRANSACTION_NOT_FOUND("해당 거래 기록이 존재하지 않습니다."),

    TRANSACTION_ACCOUNT_UNMATCH("해당 거래와 계좌가 일치하지 않습니다."),

    TRANSACTIONAMOUNT_CANCELAMOUNT_UNMATCH("거래 금액과 취소 금액이 다릅니다."),

    CANCEL_AFTER_ONE_YEAR_TRANSACTION("해당 거래가 1년이 넘었습니다."),
    INVALID_REQUEST("올바르지 않는 취소 요청 입니다.");

    private final String description;
}
