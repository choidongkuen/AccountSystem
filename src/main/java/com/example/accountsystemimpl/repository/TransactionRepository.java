package com.example.accountsystemimpl.repository;

import com.example.accountsystemimpl.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
