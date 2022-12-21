package com.example.accountsystemimpl.service;

import com.example.accountsystemimpl.domain.Account;
import com.example.accountsystemimpl.domain.AccountUser;
import com.example.accountsystemimpl.domain.Transaction;
import com.example.accountsystemimpl.dto.TransactionDto;
import com.example.accountsystemimpl.exception.AccountException;
import com.example.accountsystemimpl.exception.TransactionException;
import com.example.accountsystemimpl.repository.AccountRespository;
import com.example.accountsystemimpl.repository.AccountUserRepository;
import com.example.accountsystemimpl.repository.TransactionRepository;
import com.example.accountsystemimpl.type.AccountStatus;
import com.example.accountsystemimpl.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.accountsystemimpl.type.AccountStatus.IN_USE;
import static com.example.accountsystemimpl.type.TransactionResultType.FAIL;
import static com.example.accountsystemimpl.type.TransactionResultType.SUCCESS;
import static com.example.accountsystemimpl.type.TransactionType.CANCEL;
import static com.example.accountsystemimpl.type.TransactionType.USE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.Silent.class)
class TransactionServiceTest {


    public static final long USE_AMOUNT = 200L;
    public static final long CANCEL_AMOUNT = 200L;

    @Mock
    private AccountRespository accountRespository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountUserRepository accountUserRepository;


    @InjectMocks
    private TransactionService transactionService;


    @Test
    @DisplayName("잔액 사용 성공 테스트")
    void successUseBalance() {
        // given

        AccountUser accountUser = AccountUser
                .builder()
                .id(1L)
                .name("loopy")
                .build();

        Account account = Account.builder()
                                 .accountUser(accountUser)
                                 .accountStatus(IN_USE)
                                 .balance(10000L)
                                 .accountNumber("1000000000")
                                 .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));

        given(accountRespository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                                       .account(account)
                                       .amount(1000L)
                                       .balanceSnapshot(9000L)
                                       .transactionId("transactionId")
                                       .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        // when

        TransactionDto transactionDto = transactionService.useBalance(
                12L, "1000000000", 1000L
        );

        // then
        verify(transactionRepository, times(1)).save(captor.capture());
        assertEquals(1000L, captor.getValue().getAmount());
        assertEquals(9000L, captor.getValue().getBalanceSnapshot());
        assertEquals("1000000000", captor.getValue().getAccount().getAccountNumber());
        assertEquals(transactionDto.getBalanceSnapshot(), 9000L);
        assertEquals(transactionDto.getAccountNumber(), "1000000000");
        assertEquals(transactionDto.getAmount(), 1000L);
    }

    @Test
    @DisplayName("해당 유저 없음  - 전액 사용 실패")
    void useBalance_UserNotFound() {

        // given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        // when
        // then
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(
                        12L, "1000000000", 1000L)
        );

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    @DisplayName("해당 계좌 없음 - 전액 사용 실패")
    void useAccount_AccountNotFound() {
        // given

        AccountUser user = AccountUser.builder()
                                      .id(12L)
                                      .name("Pobi")
                                      .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRespository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1000000000", 1200L));
        // then
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    @DisplayName("계좌 소유주 다름 - 전액 사용 실패")
    void useAccountFailed_userUnMatch() {

        // given
        AccountUser pobi = AccountUser.builder()
                                      .id(12L)
                                      .name("pobi")
                                      .build();

        AccountUser harry = AccountUser.builder()
                                       .id(15L)
                                       .name("harry")
                                       .build();


        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi));

        given(accountRespository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                                               .accountUser(harry)
                                               .balance(0L)
                                               .accountNumber("1000000000")
                                               .build()));

        // when
        AccountException exception = assertThrows(AccountException.class
                , () -> transactionService.useBalance(
                        12L, "1000000000", 1000L)
        );

        // then
        assertEquals(ErrorCode.USER_ACCOUNT_UNMATCH, exception.getErrorCode());

    }

    @Test
    @DisplayName("해지 계좌는 사용 불가한 경우")
    void useAccountFailed_alreadyUnregistered() {

        // given
        AccountUser pobi = AccountUser.builder()
                                      .id(12L)
                                      .name("pobi")
                                      .build();


        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi));

        given(accountRespository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                                               .id(10L)
                                               .accountUser(pobi)
                                               .accountNumber("1000000000")
                                               .balance(1000L)
                                               .accountStatus(AccountStatus.STOP_USE)
                                               .build()));

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(
                        12L,
                        "1000000000",
                        1000L
                ));

        assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());
    }

    @Test
    @DisplayName("거래 금액이 잔액보다 큰 경우")
    void exceedAmount_UseBalance() {

        // given
        AccountUser pobi = AccountUser.builder()
                                      .id(12L)
                                      .name("pobi")
                                      .build();

        Account account = Account.builder()
                                 .id(100L)
                                 .accountUser(pobi)
                                 .accountStatus(IN_USE)
                                 .balance(2000L)
                                 .accountNumber("1000000000")
                                 .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi));

        given(accountRespository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));


        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(
                        12L,
                        "1000000000",
                        3000L
                ));

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.AMOUNT_EXCEED_BALANCE);
    }

    @Test
    @DisplayName("실패 트랜잭션 저장 성공")
    void saveFailedUseTransaction() {

        // given
        AccountUser pobi = AccountUser.builder()
                                      .id(12L)
                                      .name("pobi")
                                      .build();

        Account account = Account.builder()
                                 .id(100L)
                                 .accountUser(pobi)
                                 .accountStatus(IN_USE)
                                 .balance(10000L)
                                 .accountNumber("1000000000")
                                 .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi));

        given(accountRespository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                                       .transactionType(USE)
                                       .transactionResultType(SUCCESS)
                                       .transactionId("transactionId")
                                       .account(account)
                                       .amount(1000L)
                                       .balanceSnapshot(9000L)
                                       .transactionAt(LocalDateTime.now())
                                       .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        // when
        transactionService.saveFailedUserTransaction(

                "1000000000",
                USE_AMOUNT
        );

        // then
        verify(transactionRepository, times(1)).save(captor.capture());


        assertEquals(USE_AMOUNT, captor.getValue().getAmount());
        assertEquals(10000L, captor.getValue().getBalanceSnapshot());
        assertEquals(FAIL, captor.getValue().getTransactionResultType());

    }


    @Test
    @DisplayName("잔액 사용 취소 성공 테스트")
    void successCancelBalance() {
        // given

        AccountUser accountUser = AccountUser
                .builder()
                .id(1L)
                .name("loopy")
                .build();

        Account account = Account.builder()
                                 .accountUser(accountUser)
                                 .accountStatus(IN_USE)
                                 .balance(10000L)
                                 .accountNumber("1000000000")
                                 .build();

        Transaction transaction = Transaction.builder()
                                             .account(account)
                                             .transactionType(USE)
                                             .transactionResultType(SUCCESS)
                                             .transactionId("transactionId")
                                             .transactionAt(LocalDateTime.now())
                                             .amount(CANCEL_AMOUNT)
                                             .balanceSnapshot(9000L)
                                             .build();


        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(accountRespository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                                       .transactionType(CANCEL)
                                       .transactionResultType(SUCCESS)
                                       .account(account)
                                       .amount(CANCEL_AMOUNT)
                                       .balanceSnapshot(10000L)
                                       .transactionId("transactionId")
                                       .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        // when
        TransactionDto transactionDto = transactionService.cancelBalance(
                "transactionId", "1000000000", CANCEL_AMOUNT
        );

        // then
        verify(transactionRepository, times(1)).save(captor.capture());
        assertEquals(CANCEL_AMOUNT, captor.getValue().getAmount());
        assertEquals(10000L + CANCEL_AMOUNT, captor.getValue().getBalanceSnapshot());
        assertEquals("1000000000", captor.getValue().getAccount().getAccountNumber());
        assertEquals(SUCCESS, transactionDto.getTransactionResultType());
        assertEquals(CANCEL, transactionDto.getTransactionType());
        assertEquals(10000L, transactionDto.getBalanceSnapshot());
        assertEquals(CANCEL_AMOUNT, transactionDto.getAmount());
    }

    @Test
    @DisplayName("해당 계좌 없음 - 전액 사용 취소 실패")
    void cancelTransaction_AccountNotFound() {
        // given

        AccountUser user = AccountUser.builder()
                                      .id(12L)
                                      .name("Pobi")
                                      .build();

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(Transaction.builder()
                                                   .transactionType(CANCEL)
                                                   .transactionResultType(SUCCESS)
                                                   .amount(CANCEL_AMOUNT)
                                                   .balanceSnapshot(10000L)
                                                   .transactionId("transactionId")
                                                   .build()));

        given(accountRespository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        // when
        TransactionException exception = assertThrows(TransactionException.class,
                () -> transactionService.cancelBalance("transactionId", "1000000000", 1200L));
        // then
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());

    }

    @Test
    @DisplayName("해당 거래 없음 - 해당 거래가 존재하지 않는 경우")
    void cancelTransaction_TransactionNotFound() {
        // given

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.empty());

        // when
        TransactionException exception = assertThrows(TransactionException.class,
                () -> transactionService.cancelBalance("transactionId",
                        "1000000000",
                        199L));
        // then
        assertEquals(exception.getErrorCode(), ErrorCode.TRANSACTION_NOT_FOUND);
    }

    @Test
    @DisplayName("거래와 계좌가 다른 경우")
    void cancelTransaction_TransactionAccountUnmatch() {
        // given

        AccountUser accountUser = AccountUser
                .builder()
                .id(1L)
                .name("loopy")
                .build();

        Account pobi = Account.builder()
                              .id(12L)
                              .accountUser(accountUser)
                              .accountStatus(IN_USE)
                              .balance(10000L)
                              .accountNumber("1000000000")
                              .build();

        Account harry = Account.builder()
                               .id(13L)
                               .accountUser(accountUser)
                               .accountStatus(IN_USE)
                               .balance(10000L)
                               .accountNumber("1000000001")
                               .build();

        Transaction transaction = Transaction.builder()
                                             .account(pobi)
                                             .transactionType(USE)
                                             .transactionResultType(SUCCESS)
                                             .transactionId("transactionId")
                                             .transactionAt(LocalDateTime.now())
                                             .amount(CANCEL_AMOUNT)
                                             .balanceSnapshot(9000L)
                                             .build();


        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(accountRespository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(harry));

        // when

        TransactionException exception = assertThrows(TransactionException.class,
                () -> transactionService.cancelBalance(
                        "transactionId",
                        "1000000000",
                        CANCEL_AMOUNT
                ));

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.TRANSACTION_ACCOUNT_UNMATCH);
    }

    @Test
    @DisplayName("거래 금액과 취소 금액이 다른 경우")
    void cancelTransaction_Amount_CancelAmount_Unmatch() {
        // given

        AccountUser accountUser = AccountUser
                .builder()
                .id(1L)
                .name("loopy")
                .build();

        Account account = Account.builder()
                              .id(12L)
                              .accountUser(accountUser)
                              .accountStatus(IN_USE)
                              .balance(10000L)
                              .accountNumber("1000000000")
                              .build();

        Transaction transaction = Transaction.builder()
                                             .account(account)
                                             .transactionType(USE)
                                             .transactionResultType(SUCCESS)
                                             .transactionId("transactionId")
                                             .transactionAt(LocalDateTime.now())
                                             .amount(CANCEL_AMOUNT)
                                             .balanceSnapshot(9000L)
                                             .build();


        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(accountRespository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        // when

        TransactionException exception = assertThrows(TransactionException.class,
                () -> transactionService.cancelBalance(
                        "transactionId",
                        "1000000000",
                        20000L
                ));

        // then
        assertEquals(exception.getErrorCode(),
                ErrorCode.TRANSACTIONAMOUNT_CANCELAMOUNT_UNMATCH);
    }

    @Test
    @DisplayName("거래 금액과 취소 금액이 다른 경우")
    void cancelTransaction_after_oneYear_transaction() {

        // given
        AccountUser accountUser = AccountUser
                .builder()
                .id(1L)
                .name("loopy")
                .build();

        Account account = Account.builder()
                                 .id(12L)
                                 .accountUser(accountUser)
                                 .accountStatus(IN_USE)
                                 .balance(10000L)
                                 .accountNumber("1000000000")
                                 .build();

        Transaction transaction = Transaction.builder()
                                             .account(account)
                                             .transactionType(USE)
                                             .transactionResultType(SUCCESS)
                                             .transactionId("transactionId")
                                             .transactionAt(LocalDateTime.now().minusYears(10))
                                             .amount(CANCEL_AMOUNT)
                                             .balanceSnapshot(9000L)
                                             .build();


        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(accountRespository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        // when

        TransactionException exception = assertThrows(TransactionException.class,
                () -> transactionService.cancelBalance(
                        "transactionId",
                        "1000000000",
                        CANCEL_AMOUNT
                ));

        // then
        assertEquals(exception.getErrorCode(),
                ErrorCode.CANCEL_AFTER_ONE_YEAR_TRANSACTION);
    }
}