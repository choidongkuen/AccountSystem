package com.example.accountsystemimpl.service;

import com.example.accountsystemimpl.domain.Account;
import com.example.accountsystemimpl.domain.AccountUser;
import com.example.accountsystemimpl.dto.AccountDto;
import com.example.accountsystemimpl.exception.AccountException;
import com.example.accountsystemimpl.repository.AccountRespository;
import com.example.accountsystemimpl.repository.AccountUserRepository;
import com.example.accountsystemimpl.type.AccountStatus;
import com.example.accountsystemimpl.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.accountsystemimpl.type.ErrorCode.ACCOUNT_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


// Mockito 사용
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {


    @Mock
    private AccountRespository accountRespository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    @DisplayName("계좌 생성 성공")
    void createAccountSuccess() {

        AccountUser accountUser = AccountUser.builder()
                                             .id(12L)
                                             .name("pobi").build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));


        given(accountRespository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(Account.builder()
                                               .accountNumber("1000000013").build()));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        given(accountRespository.save(any()))
                .willReturn(Account.builder()
                                   .accountUser(accountUser)
                                   .accountNumber("1000000013").build());
        // given
        // when
        AccountDto dto = accountService.createAccount(1L, 1000L);
        // then

        assertEquals(12L, dto.getUserId());
        assertEquals("1000000013", dto.getAccountNumber());
    }

    @Test
    @DisplayName("계좌 생성 실패(해당 사용자 존재x)")
        // 해당 사용자가 존재하지 않는 경우
    void createAccount_UserNotFound() {

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());


        // createAccount 메소드 실행시, 예외 발생할 것이다.
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 1000L));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    @DisplayName("계좌 생성 실패(해당 사용자 계좌 10개 이상)")
    void createAccount_maxAccountIs10() {

        AccountUser user = AccountUser.builder()
                                      .id(12L)
                                      .name("pobi")
                                      .build();

        // given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRespository.countByAccountUser(user))
                .willReturn(10);

        // when

        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 1000L));
        // then

        assertEquals(ErrorCode.MAX_COUNT_PER_USER_TEN, exception.getErrorCode());
    }


    @Test
    @DisplayName("계좌 해지 성공")
    void deleteAccountSuccess() {

        AccountUser user = AccountUser.builder()
                                      .id(12L)
                                      .name("pobi")
                                      .build();

        // given(조건)
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRespository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                                               .accountUser(user)
                                               .balance(0L)
                                               .accountNumber("1000000012").build())
                );

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        // when(기능 수행)
        AccountDto accountDto = accountService.deleteAccount(
                1L, "1234567890");
        // then(예상 결과)
        verify(accountRespository, times(1)).save(captor.capture());
        assertEquals(12L, accountDto.getUserId());
        assertEquals("1000000012", captor.getValue().getAccountNumber());
        assertEquals(AccountStatus.STOP_USE, captor.getValue().getAccountStatus());
    }

    @Test
    @DisplayName("계좌 해지 실패(해당 유저 없음)")
    void deleteAccountUserNotFound() {

        given(accountRespository.findById(anyLong()))
                .willReturn(Optional.empty());

        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));

        // then(예상 결과)
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    @DisplayName("계좌 해지 실패(계좌 없음)")
    void deleteAccountNotFound() {

        AccountUser accountUser = AccountUser.builder()
                                             .id(12L)
                                             .name("LUPI")
                                             .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));


        given(accountRespository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));

        // then
        assertEquals(exception.getErrorCode(), ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("계좌 소유주 다름")
    void deleteAccountFailed_userUnMatch() {
        // given
        AccountUser pobi = AccountUser.builder()
                                      .id(13L)
                                      .name("pobi")
                                      .build();

        AccountUser harry = AccountUser.builder()
                                       .id(12L)
                                       .name("harry")
                                       .build();



        // 주어진 userId 통해 찾은 AccountUser -> pobi
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi));


        // 주어진 계좌번호로 찾은 AccountUser -> harry
        given(accountRespository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                                               .accountUser(harry)
                                               .accountNumber("1000000000")
                                               .build()));


        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));
        // userId : 13
        // accountNumber : "1000000000"

        // then
        assertEquals(ErrorCode.USER_ACCOUNT_UNMATCH, exception.getErrorCode());
    }

    @Test
    @DisplayName("해지 계좌는 잔액이 없어야 한다.")
    void deleteAccountFailed_BalanceNotEmpty() {
        // given
        AccountUser accountUser = AccountUser.builder()
                                             .id(1L)
                                             .name("harry")
                                             .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));

        given(accountRespository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                                               .id(10L)
                                               .balance(1000L)
                                               .accountUser(accountUser)
                                               .accountNumber("100000000")
                                               .build()));

        AccountException exception = assertThrows(AccountException.class, ()
                -> accountService.deleteAccount(1L, "1234567890"));


        assertEquals(ErrorCode.ACCOUNT_HAS_BALANCE, exception.getErrorCode());


        // when
        // then
    }

//    @Test
//    @DisplayName("계좌 확인 테스트")
//    void successGetAccountByUserId() {
//        // given
//        // AccountUser 객체 2번 사용됨
//        AccountUser jack = AccountUser.builder()
//                                      .id(12L)
//                                      .name("jack")
//                                      .build();
//
//        List<Account> accounts =
//                Arrays.asList(Account.builder()
//                                     .accountUser(jack)
//                                     .balance(2000L)
//                                     .accountNumber("1234567890")
//                                     .build(),
//                        Account.builder()
//                               .accountUser(jack)
//                               .accountNumber("1000000000")
//                               .balance(1000L)
//                               .build());
//
//        given(accountUserRepository.findById(anyLong()))
//                .willReturn(Optional.of(jack));
//
//        given(accountRespository.findByAccountUser(jack))
//                .willReturn(accounts);
//
//
//        // when
//        List<AccountDto> result = accountService.getAccountsByUserId(100L);
//        // then
//
//        assertEquals(2, result.size());
//        assertEquals(2000, result.get(0).getBalance());
//        assertEquals(1000, result.get(1).getBalance());
//        assertEquals("1000000000", result.get(1).getAccountNumber());
//        assertEquals("1234567890", result.get(0).getAccountNumber());
//    }
//
//
//    @Test
//    @DisplayName("계좌 확인 실패(사용자 ID 없음)")
//    void failGetAccountByUserId() {
//        // given
//        given(accountUserRepository.findById(anyLong()))
//                .willReturn(Optional.empty());
//
//        AccountException exception = assertThrows(AccountException.class,
//                () -> accountService.getAccountsByUserId(1L));
//        // then
//        assertEquals(exception.getErrorCode(), ErrorCode.USER_NOT_FOUND);
//    }


    @Test
    @DisplayName("getAccontsByUserId 성공 테스트")
    void getAccountsByUSerIdSUCCESSTEST(){

        // given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("동근")
                .build();

        List<Account> accounts = Arrays.asList(
                Account.builder()
                        .accountUser(user)
                        .accountNumber("1234567890")
                        .balance(12000L)
                        .build(),
                Account.builder()
                        .accountUser(user)
                        .accountNumber("2345678901")
                        .balance(20000L)
                        .build()
        );

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRespository.findByAccountUser(any()))
                .willReturn(accounts);

        // when

        List<AccountDto> accountDtos = accountService.getAccountsByUserId(12L);


        // then

        assertEquals(exception.getErrorCode(), ErrorCode.USER_NOT_FOUND);
    }


        assertThat(accountDtos.size()).isEqualTo(2);
        assertEquals("1234567890", accountDtos.get(0).getAccountNumber());
     }

}