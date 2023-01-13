package com.example.accountsystemimpl.domain;


import com.example.accountsystemimpl.type.TransactionType;
import com.example.accountsystemimpl.type.TransactionResultType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@EntityListeners(AuditingEntityListener.class)

public class Transaction {

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

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


}

// 수정했습니다 확인 부탁드립니다.
// 두번째 수정입니다. 다시 확인 부탁드릴께요!