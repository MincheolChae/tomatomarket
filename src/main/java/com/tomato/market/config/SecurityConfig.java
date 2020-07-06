package com.tomato.market.config;

import com.tomato.market.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccountService accountService;
    private final DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable().headers().frameOptions().disable(); //h2-console 사용하기 위한 설정

        http.authorizeRequests()
                .antMatchers("/", "/login", "/sign-up", "/check-email-token", "/find-id",
                        "/email-login", "/login-by-email", "/search", "/oauth2/**").permitAll()
                .antMatchers("/css/**", "/img/**", "/js/**", "/fonts/**", "/icon/**", "/node_modules/**", "/h2-console/**").permitAll()  //정적 요소들 path 허용
                .antMatchers(HttpMethod.GET, "/profile/*").permitAll()
                .anyRequest().authenticated();

        http.formLogin().loginPage("/login").permitAll();

        http.logout().logoutSuccessUrl("/").permitAll();

        http.rememberMe()
                .userDetailsService(accountService)
                .tokenRepository(tokenRepository())
                .tokenValiditySeconds(86400);
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }


}
