package com.tomato.market.product.event;

import com.tomato.market.account.AccountPredicates;
import com.tomato.market.account.domain.Account;
import com.tomato.market.account.domain.AccountRepository;
import com.tomato.market.config.AppProperties;
import com.tomato.market.mail.EmailMessage;
import com.tomato.market.mail.EmailService;
import com.tomato.market.notification.Notification;
import com.tomato.market.notification.NotificationRepository;
import com.tomato.market.notification.NotificationType;
import com.tomato.market.product.Product;
import com.tomato.market.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Slf4j
@Async
@Transactional
@Component
@RequiredArgsConstructor
public class ProductEventListener {

    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleProductCreatedEvent(ProductCreatedEvent productCreatedEvent) {
        Product product = productRepository.findProductWithTagsAndLocationsById(productCreatedEvent.getProduct().getId());
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndLocations(product.getTags(), product.getLocations()));

        accounts.forEach(account -> {
            if(account.isNewProductNotiByEmail()){
                Context context = new Context();
                context.setVariable("link", "/product/" + product.getId());
                context.setVariable("name", account.getName());
                context.setVariable("nickname", account.getNickname());
                context.setVariable("linkName", product.getTitle());
                context.setVariable("message", "동네에 관심 키워드와 관련된 매물이 등록되었습니다.");
                context.setVariable("host", appProperties.getHost());
                String message = templateEngine.process("mail/email-link", context);

                EmailMessage emailMessage = EmailMessage.builder()
                        .subject("토마토 마켓, '"+ product.getTitle() + "'  매물이 등록되었습니다.")
                        .to(account.getEmail())
                        .message(message)
                        .build();

                emailService.sendEmail(emailMessage);
            }

            if(account.isNewProductNotiByWeb()){
                Notification notification = new Notification();
                notification.setTitle(product.getTitle());
                notification.setLink("/product/" + product.getId());
                notification.setChecked(false);
                notification.setCreatedLocalDateTime(LocalDateTime.now());
                notification.setMessage(product.getDescription());
                notification.setAccount(account);
                notification.setNotificationType(NotificationType.PRODUCT_CREATED);
                notificationRepository.save(notification);
            }
        });

    }
}
