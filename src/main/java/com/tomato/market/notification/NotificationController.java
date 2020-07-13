package com.tomato.market.notification;

import com.tomato.market.account.CurrentAccount;
import com.tomato.market.account.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

import static com.tomato.market.notification.NotificationType.PRODUCT_CREATED;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public String getNotifications(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedLocalDateTimeDesc(account, false);
        long numberOfChecked = notificationRepository.countByAccountAndChecked(account, true);
        putCategorizedNotifications(model, notifications, numberOfChecked, notifications.size());
        model.addAttribute("isNew", true);
        notificationService.markAsRead(notifications);
        return "account/notifications";
    }

    @GetMapping("/notifications/old")
    public String getOldNotifications(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedLocalDateTimeDesc(account, true);
        long numberOfNotChecked = notificationRepository.countByAccountAndChecked(account, false);
        putCategorizedNotifications(model, notifications, notifications.size(), numberOfNotChecked);
        model.addAttribute("isNew", false);
        return "account/notifications";
    }

    @DeleteMapping("/notifications")  //DeleteMapping 하려면, application.properties 에 설정 추가해야함
    public String deleteNotifications(@CurrentAccount Account account) {
        notificationRepository.deleteByAccountAndChecked(account, true);
        return "redirect:/notifications";
    }

    private void putCategorizedNotifications(Model model, List<Notification> notifications, long numberOfChecked, long numberOfNotChecked) {
        List<Notification> newProductNotifications = new ArrayList<>();
        for (var notification : notifications) {
            if (notification.getNotificationType().equals(PRODUCT_CREATED)) {
                newProductNotifications.add(notification);
            }
        }
        model.addAttribute("numberOfNotChecked", numberOfNotChecked);
        model.addAttribute("numberOfChecked", numberOfChecked);
        model.addAttribute("notifications", notifications);
        model.addAttribute("newProductNotifications", newProductNotifications);
    }

}