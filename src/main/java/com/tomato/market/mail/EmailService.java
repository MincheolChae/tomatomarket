package com.tomato.market.mail;

import org.springframework.scheduling.annotation.EnableAsync;

public interface EmailService {
    void sendEmail(EmailMessage emailMessage);
}
