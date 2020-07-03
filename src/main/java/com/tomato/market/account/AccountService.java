package com.tomato.market.account;

import com.tomato.market.account.domain.Account;
import com.tomato.market.account.domain.AccountRepository;
import com.tomato.market.account.profile.ProfileModifyForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Arrays;

@RequiredArgsConstructor
@Transactional
@Service
public class AccountService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;  //AppConfig 에 꼭 빈으로 등록해두어야 할까?? 거기 있는건 메소드네
    private final ModelMapper modelMapper;  //ModelMapper 의 map은 source에서 destination으로 테스트를 거친 뒤 매핑해주는듯? (정보 찾아보기)
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

    public Account processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account = modelMapper.map(signUpForm, Account.class);
        account.generateEmailCheckToken();
        return accountRepository.save(account);
    }

    private void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("토마토마켓 이메일 인증");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() + "&email=" + newAccount.getEmail());
        javaMailSender.send(mailMessage);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(username);
        if (account == null) {
            account = accountRepository.findByNickname(username);
        }
        if (account == null) {
            throw new UsernameNotFoundException(username);
        }
        return new UserAccount(account);
    }

    public Account getAccount(String nickname) {
        Account account = accountRepository.findByNickname(nickname);
        if (account == null) {
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
        }
        return account;
    }

    public void completeSignUp(Account account) {
        account.setEmailVerified(true);
        account.setJoinedAt(LocalDateTime.now());
        login(account);
    }

    public void modifyProfile(Account account, ProfileModifyForm profileModifyForm) {
        modelMapper.map(profileModifyForm, account);
        accountRepository.save(account);
    }
}