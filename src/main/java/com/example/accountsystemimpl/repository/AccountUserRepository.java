package com.example.accountsystemimpl.repository;

import com.example.accountsystemimpl.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountUerRepository extends JpaRepository<AccountUser, Long> {

}
