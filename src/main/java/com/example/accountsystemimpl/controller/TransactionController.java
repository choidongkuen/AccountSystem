package com.example.accountsystemimpl.controller;


import com.example.accountsystemimpl.dto.CancelBalance;
import com.example.accountsystemimpl.dto.QueryTransactionResponse;
import com.example.accountsystemimpl.dto.UseBalance;
import com.example.accountsystemimpl.exception.AccountException;
import com.example.accountsystemimpl.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    private final TransactionService transactionService;

    // 잔액 사용 API
    @PostMapping("/transaction/use")
    public UseBalance.Response useBalance(
            @RequestBody @Valid UseBalance.Request request
    ) {

        try {
            return UseBalance.Response.fromTransactionDto(
                    transactionService.useBalance(request.getUserId(),
                            request.getAccountNumber(),request.getAmount())
            );
        } catch (AccountException e) {
            log.error("Failed to use balance");
            transactionService.saveFailedUserTransaction(
                    request.getAccountNumber(),request.getAmount()
            );
            throw e;
        }
    }

    // 잔액 사용 취소 API
    @PostMapping("/transaction/cancel")
    public CancelBalance.Response cancelBalance(
            @RequestBody @Valid CancelBalance.Request request
    ){

        try{
            return CancelBalance.Response.from(
                    transactionService.cancelBalance(request.getTransactionId()
                    ,request.getAccountNumber(), request.getAmount()));

        }catch (AccountException e){
            log.error("Failed to cancel balance");
            transactionService.saveFailedCancelTransaction(request.getAccountNumber(),
                    request.getAmount()
            );

            throw e;
        }
    }

    @GetMapping("/transaction/{transactionId}")
    public QueryTransactionResponse queryTransaction(
            @PathVariable String transactionId
    ) {

        return QueryTransactionResponse.fromTransactionDto(
                transactionService.queryTransaction(transactionId)
        );

    }
}
