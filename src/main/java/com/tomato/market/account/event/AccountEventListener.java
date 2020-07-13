package com.tomato.market.account.event;

import com.tomato.market.account.domain.Account;
import com.tomato.market.account.domain.AccountRepository;
import com.tomato.market.config.AppProperties;
import com.tomato.market.mail.EmailMessage;
import com.tomato.market.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Optional;

@Slf4j
@Async
@Transactional
@Component
@RequiredArgsConstructor
public class AccountEventListener {

    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    @EventListener
    public void handleSendingEmailEvent(SendingEmailEvent sendingEmailEvent) {

        Account newAccount = sendingEmailEvent.getAccount();

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
}
