package com.tomato.market;

import com.tomato.market.account.domain.Account;
import com.tomato.market.account.domain.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootApplication
public class MarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner runner(AccountRepository accountRepository) throws Exception{
//        return (args) -> {
//            Account account = accountRepository.save(Account.builder()
//                .email("cmc752@naver.com")
//                .password(new BCryptPasswordEncoder().encode("12341234"))
//                .nickname("채민철")
//                .phone("01012341234")
//                .emailCheckToken(UUID.randomUUID().toString())
//                .emailCheckTokenGeneratedAt(LocalDateTime.now()).build());
//        };
//    }
}
