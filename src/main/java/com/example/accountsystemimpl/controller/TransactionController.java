package com.example.accountsystemimpl.controller;


import com.example.accountsystemimpl.dto.DeleteAccount;
import com.example.accountsystemimpl.dto.TransactionDto;
import com.example.accountsystemimpl.dto.UseBalance;
import com.example.accountsystemimpl.exception.AccountException;
import com.example.accountsystemimpl.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 잔액 관련 컨틀롤러
 * 1. 잔액 사용
 * 2. 잔액 사용 취소
 * 3. 거래 확인
 */

@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transcationService;


    @PostMapping("/transaction/use")
    public UseBalance.Response useBalance(
            @RequestBody @Valid UseBalance.Request request
    ) {


        try {
            return UseBalance.Response.fromAccountDto(
                    transcationService.useBalance(request)
            );
        } catch (AccountException e) {
            log.error("Failed to use balance");
            transcationService.saveFailedTransaction(request);
            throw e;
        }
    }
}
