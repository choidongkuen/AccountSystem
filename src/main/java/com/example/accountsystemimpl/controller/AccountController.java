package com.example.accountsystemimpl.controller;

import com.example.accountsystemimpl.aop.AccountLock;
import com.example.accountsystemimpl.dto.AccountInfo;
import com.example.accountsystemimpl.dto.CreateAccount;
import com.example.accountsystemimpl.dto.DeleteAccount;
import com.example.accountsystemimpl.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // 계좌 생성
    // CreateAccount.Request -> Accont -> Account -> CreatedAccount.Response
    @PostMapping("/account")
    @AccountLock
    public CreateAccount.Response createAccount(
            @RequestBody @Valid CreateAccount.Request request) {

        return CreateAccount.Response.FromAccountDto(
                accountService.createAccount(
                        request.getUserId(),
                        request.getInitialBalance())
        );
    }


    // 계좌 해지
    @DeleteMapping("/account")
    @AccountLock
    public DeleteAccount.Response deleteAccount(
            @RequestBody @Valid DeleteAccount.Request request) {


        return DeleteAccount.Response.fromAccountDto(
                accountService.deleteAccount(
                        request.getUserId(),
                        request.getAccountNumber()
                )
        );
    }

    // 계좌 확인
    @GetMapping("/account")
    @AccountLock
    public List<AccountInfo> getAccountByUserId(
            @RequestParam("userId") Long userId
    ) {

        return accountService.getAccountsByUserId(userId)
                             .stream().map(AccountInfo::fromAccountDto)
                             .collect(Collectors.toList());

    }
}
