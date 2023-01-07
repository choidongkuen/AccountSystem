package com.example.accountsystemimpl.controller;

import com.example.accountsystemimpl.domain.Account;
import com.example.accountsystemimpl.dto.AccountDto;
import com.example.accountsystemimpl.dto.CreateAccount;
import com.example.accountsystemimpl.dto.DeleteAccount;
import com.example.accountsystemimpl.service.AccountService;
import com.example.accountsystemimpl.type.AccountStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


// WebMvcTest 사용
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    // 계좌 생성 컨트롤러
    @Test
    void successCreateAccount() {
        // given
        given(accountService.createAccount(anyLong(), anyLong()))
                .willReturn(AccountDto.builder()
                                      .userId(1L)
                                      .accountNumber("1234567890")
                                      .balance(10000L)
                                      .registeredAt(LocalDateTime.now())
                                      .unRegisteredAt(LocalDateTime.now())
                                      .build());
        // when
        // then
        try {
            mockMvc.perform(post("/account")
                           .contentType(MediaType.APPLICATION_JSON)
                           .content(objectMapper.writeValueAsString( // Json 타입으로 전송
                                   new CreateAccount.Request(1L, 10000L)
                           ))).andExpect(status().isOk())
                   .andExpect(jsonPath("$.userId").value(1))
                   .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                   .andDo(print());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // 계좌 확인 컨트롤러
    @Test
    void successGetAccount() {

        // given
        given(accountService.getAccount(anyLong()))
                .willReturn(Account.builder()
                                   .accountNumber("3456")
                                   .accountStatus(AccountStatus.IN_USE)
                                   .build());
        // when
        // then
        try {
            mockMvc.perform(get("/account/876")) // PathVariable
                   .andDo(print())
                   .andExpect(MockMvcResultMatchers.jsonPath("$.accountNumber").value("3456"))
                   .andExpect(MockMvcResultMatchers.jsonPath("$.accountStatus").value("IN_USE"))
                   .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    // 계좌 해지 컨트롤러
    @Test
    @DisplayName("계좌 해지 테스트")
    void successDeleteAccount() {
        // given
        given(accountService.deleteAccount(anyLong(), anyString()))
                .willReturn(AccountDto.builder()
                                      .userId(1L)
                                      .accountNumber("1234567890")
                                      .registeredAt(LocalDateTime.now())
                                      .unRegisteredAt(LocalDateTime.now())
                                      .build());
        // when
        // then
        try {
            mockMvc.perform(delete("/account")
                           .contentType(MediaType.APPLICATION_JSON)
                           .content(objectMapper.writeValueAsString(
                                   new DeleteAccount.Request(333L, "1234567890")
                           )))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.userId").value(1))
                   .andExpect(jsonPath("$.accountNumber").value("!234567890"))
                   .andDo(print());

        } catch (Exception e) {

        }
    }

    @Test
    @DisplayName("계좌 확인 테스트")
    void successCheckAccount() {

        // given
        List<AccountDto> accountDto
                = Arrays.asList(AccountDto.builder()
                                          .userId(1L)
                                          .accountNumber("1234567890")
                                          .balance(10000L)
                                          .registeredAt(LocalDateTime.now())
                                          .unRegisteredAt(LocalDateTime.now())
                                          .build(),
                AccountDto.builder()
                          .userId(1L)
                          .accountNumber("1234567771")
                          .balance(20000L)
                          .registeredAt(LocalDateTime.now())
                          .unRegisteredAt(LocalDateTime.now())
                          .build());

        given(accountService.getAccountsByUserId(anyLong()))
                .willReturn(accountDto);
        // then
        try {
            mockMvc.perform(get("/account?userId=1"))
                   .andExpect(jsonPath("$[0].accountNumber").value("1234567890"))
                   .andExpect(jsonPath("$[0].balance").value("10000"))
                   .andExpect(jsonPath("$[1].accountNumber").value("1234567771"))
                   .andExpect(jsonPath("$[0].balance").value("20000"))
                   .andDo(print());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}