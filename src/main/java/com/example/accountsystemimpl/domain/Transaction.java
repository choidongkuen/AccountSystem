package com.example.accountsystemimpl.domain;


import com.example.accountsystemimpl.type.TransactionResultType;
import com.example.accountsystemimpl.type.TransactionType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
public class Transaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING) // 기본적으로 EnumType.Ordinal
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private TransactionResultType transactionResultType; // 성공 or 실패

    @ManyToOne
    private Account account;
    private Long amount;
    private Long balanceSnapshot;

    private String transactionId; // 보안을 위해 별도 생성

    private LocalDateTime transactionAt;
}
