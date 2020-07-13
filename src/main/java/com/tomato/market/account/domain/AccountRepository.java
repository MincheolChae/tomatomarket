package com.tomato.market.account.domain;

import com.tomato.market.product.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface AccountRepository extends JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account> {
    boolean existsByEmail(String email);
    Account findByEmail(String email);

    boolean existsByNickname(String name);
    Account findByNickname(String name);

    Account findByNameAndPhone(String name, String phone);

}