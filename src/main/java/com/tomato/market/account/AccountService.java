package com.tomato.market.account;

import com.tomato.market.account.domain.Account;
import com.tomato.market.account.domain.AccountRepository;
import com.tomato.market.account.profile.NotificationForm;
import com.tomato.market.account.profile.PasswordForm;
import com.tomato.market.account.profile.ProfileModifyForm;
import com.tomato.market.config.AppProperties;
import com.tomato.market.location.Location;
import com.tomato.market.tag.Tag;
import com.tomato.market.mail.EmailMessage;
import com.tomato.market.mail.EmailService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Transactional
@Service
public class AccountService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;  //AppConfig 에 꼭 빈으로 등록해두어야 할까?? 거기 있는건 메소드네
    private final ModelMapper modelMapper;  //ModelMapper 의 map은 source에서 destination으로 테스트를 거친 뒤 매핑해주는듯? (정보 찾아보기)
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final AppProperties appProperties;
    private final TemplateEngine templateEngine;

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

    public void sendSignUpConfirmEmail(Account newAccount) {
        Context context = new Context();
        context.setVariable("link", "/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        context.setVariable("name", newAccount.getName());
        context.setVariable("nickname", newAccount.getNickname());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "토마토 마켓 서비스를 이용하려면 아래 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/email-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(newAccount.getEmail())
                .subject("토마토 마켓, 회원 가입 인증")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
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
        account.completeSignUp();
        login(account);
    }

    public void modifyProfile(Account account, ProfileModifyForm profileModifyForm) {
        modelMapper.map(profileModifyForm, account);
        accountRepository.save(account);
        login(account);   //닉네임 바뀌면 로그인처리해줘야 principal 객체에 반영됨
    }

    public void modifyPassword(Account account, PasswordForm passwordForm) {
        account.setPassword(passwordEncoder.encode(passwordForm.getNewPassword()));
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, NotificationForm notificationForm) {
        modelMapper.map(notificationForm, account);
        accountRepository.save(account);
    }

    public Set<Tag> getTags(Account account) {
        Optional<Account> foundAccountById = accountRepository.findById(account.getId());
        return foundAccountById.orElseThrow(NoSuchElementException::new).getTags();
    }

    public void addTag(Account account, Tag tag) {
        Optional<Account> accountFoundById = accountRepository.findById(account.getId());
        accountFoundById.ifPresent(acc -> acc.getTags().add(tag));
    }

    public void removeTag(Account account, Tag tag) {
        Optional<Account> accountFoundById = accountRepository.findById(account.getId());
        accountFoundById.ifPresent(acc -> acc.getTags().remove(tag));
    }


    public Set<Location> getLocations(Account account) {
        Optional<Account> accountFoundById = accountRepository.findById(account.getId());
        return accountFoundById.orElseThrow(NoSuchElementException::new).getLocations();
    }

    public void addLocation(Account account, Location location) {
        Optional<Account> accountFoundById = accountRepository.findById(account.getId());
        accountFoundById.ifPresent(acc -> acc.getLocations().add(location));
    }

    public void removeLocation(Account account, Location location) {
        Optional<Account> accountFoundById = accountRepository.findById(account.getId());
        accountFoundById.ifPresent(acc -> acc.getLocations().remove(location));
    }

    public boolean isValidNickName(Account account, ProfileModifyForm profileModifyForm) {  //DB에 어카운트가 없으면 true, 있지만 같을 경우도 true 나머지 false
        Account foundAccount = accountRepository.findByNickname(profileModifyForm.getNickname());
        if (foundAccount == null) {
            return true;
        } else {
            if (account.getNickname().equals(foundAccount.getNickname())) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void sendLoginLink(Account account) {
        Context context = new Context();
        context.setVariable("link", "/login-by-email?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        context.setVariable("name", account.getName());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", "토마토마켓 로그인하기");
        context.setVariable("message", "로그인 하려면 아래 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/email-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject("토마토마켓, 로그인 링크")
                .message(message)
                .build();
        emailService.sendEmail(emailMessage);
    }
}