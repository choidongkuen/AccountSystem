package com.example.accountsystem.controller;


import com.example.accountsystem.dto.CreateAccount;
import com.example.accountsystem.service.AccountService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;




    // 계좌 생성
    // CreateAccount.Request -> Accont -> Account -> CreatedAccount.Response
    @PostMapping("/acount")
    public CreateAccount.Response createAccount(@RequestBody @Valid CreateAccount.Request request){

        return CreateAccount.Response.FromAccountDto(
                accountService.createAccount(
                        request.getUserId(),
                        request.getInitialBalance())
        );
    }

}
