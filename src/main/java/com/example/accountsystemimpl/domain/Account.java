package com.example.accountsystemimpl.domain;


import com.example.accountsystemimpl.exception.AccountException;
import com.example.accountsystemimpl.type.AccountStatus;
import com.example.accountsystemimpl.type.ErrorCode;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@Table
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Account extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // 다대일 단 방향
    @ManyToOne
    private AccountUser accountUser;

    @Column(unique = true)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    private Long balance;

    private LocalDateTime registeredAt;

    private LocalDateTime unRegisteredAt;

    public void useBalance(Long amount){

        if(this.balance < amount){
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
        this.balance -= amount;
    }

    public void cancelBalance(Long amount){

        if(amount < 0){
            throw new AccountException(ErrorCode.INVALID_REQUEST);
        }

        this.balance += amount;
    }
}
