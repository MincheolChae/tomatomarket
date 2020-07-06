package com.tomato.market.account.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByEmail(String email);
    Account findByEmail(String email);

    boolean existsByNickname(String name);
    Account findByNickname(String name);

    Account findByNameAndPhone(String name, String phone);
}