package com.example.accountsystemimpl.controller;

import com.example.accountsystemimpl.dto.CancelBalance;
import com.example.accountsystemimpl.dto.TransactionDto;
import com.example.accountsystemimpl.dto.UseBalance;
import com.example.accountsystemimpl.dto.UseBalance.Request;
import com.example.accountsystemimpl.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static com.example.accountsystemimpl.type.TransactionResultType.SUCCESS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("잔액 사용 성공")
    void successUseBalance(){

        // given
        given(transactionService.useBalance(anyLong(), anyString(), anyLong()))
                .willReturn(TransactionDto.builder()
                                .accountNumber("1000000000")
                                .transactionAt(LocalDateTime.now())
                                .amount(12345L)
                                .transactionId("transactionId")
                                .transactionResultType(SUCCESS)
                                .build());
        // when
        // then
        try {
            mockMvc.perform(post("/transaction/use")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(
                            new UseBalance.Request(1L, "1111111111",1212L)
                    ))).andExpect(status().isOk())
                    .andExpect(jsonPath("$.accountNumber").value("1000000000"))
                    .andExpect(jsonPath("$.transactionId").value("transactionId"))
                    .andExpect(jsonPath("$.amount").value(12345L))
                    .andExpect(jsonPath("$.transactionResultType").value("SUCCESS"))
                    .andDo(print());


        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @Test
    @DisplayName("잔액 사용 취소 성공")
    void successCancelBalance(){

        // given
        given(transactionService.cancelBalance(anyString(), anyString(), anyLong()))
                .willReturn(TransactionDto.builder()
                                          .accountNumber("1000000000")
                                          .transactionAt(LocalDateTime.now())
                                          .amount(12345L)
                                          .transactionId("transactionId")
                                          .transactionResultType(SUCCESS)
                                          .build());
        // when
        // then
        try {
            mockMvc.perform(post("/transaction/cancel")
                           .contentType(MediaType.APPLICATION_JSON)
                           .content(objectMapper.writeValueAsString(
                                   new CancelBalance.Request("transactionId", "1111111111",1212L)
                           ))).andExpect(status().isOk())
                   .andExpect(jsonPath("$.accountNumber").value("1000000000"))
                   .andExpect(jsonPath("$.transactionId").value("transactionId"))
                   .andExpect(jsonPath("$.amount").value(12345L))
                   .andExpect(jsonPath("$.transactionResultType").value("SUCCESS"))
                   .andDo(print());


        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}