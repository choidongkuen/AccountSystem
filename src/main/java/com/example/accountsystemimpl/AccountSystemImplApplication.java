package com.example.accountsystemimpl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AccountSystemImplApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountSystemImplApplication.class, args);
    }

}
