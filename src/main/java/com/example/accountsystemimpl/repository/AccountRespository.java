package com.example.accountsystem.repository;

import com.example.accountsystem.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AccountRespository extends JpaRepository<Account,Long> {

    Optional<Account> findFirstByOrderByIdDesc();

    Optional<Account> findLastByOrderById();
}
