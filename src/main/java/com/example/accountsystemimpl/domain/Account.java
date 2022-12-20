package com.example.accountsystem.domain;


import com.example.accountsystem.type.AccountStatus;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Slf4j
@Getter
@Builder
@Table
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    private Long balance;


    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;

    @ManyToOne
    private AccountUser accountUser;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
private LocalDateTime updatedAt;

}
