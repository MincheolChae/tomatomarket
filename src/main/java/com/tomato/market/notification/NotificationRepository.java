package com.tomato.market.notification;

import com.tomato.market.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    long countByAccountAndChecked(Account account, boolean checked);

    @Transactional
    List<Notification> findByAccountAndCheckedOrderByCreatedLocalDateTimeDesc(Account account, boolean checked);

    @Transactional
    void deleteByAccountAndChecked(Account account, boolean checked);
}
