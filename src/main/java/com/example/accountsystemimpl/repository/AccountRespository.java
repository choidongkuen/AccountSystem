package com.example.accountsystemimpl.repository;

import com.example.accountsystemimpl.domain.Account;
import com.example.accountsystemimpl.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AccountRespository extends JpaRepository<Account,Long> {

    Optional<Account> findFirstByOrderByIdDesc();
    Optional<Account> findByAccountNumber(String accountNumber);

    Integer countByAccountUser(AccountUser accountUser);

    List<Account> findByAccountUser(AccountUser user);






}
