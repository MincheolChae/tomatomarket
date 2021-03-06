package com.tomato.market.account;

import com.tomato.market.account.domain.Account;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Arrays;

@Getter
public class UserAccount extends User {

    private Account account;

    public UserAccount(Account account) {
        super(account.getNickname(),   //principal 로 사용할 username 인듯?
                account.getPassword(),
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        this.account = account;
    }
}
