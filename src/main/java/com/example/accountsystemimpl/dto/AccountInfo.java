package com.example.accountsystemimpl.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter

// Why? Dto 를 여러개로?
// 다목적이면 -> 복잡한 상황이 생기면 -> 의도치 않은 장애 발생 가능성
// 각 기능에 맞춘 클래스를 생성하자!
// 카메라 + 게임 + tv 기능의 스마트폰  vs 카메라, 게임, tv

public class AccountInfo {

    private String accountNumber;

    private Long balance;



}
