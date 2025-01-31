package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.model.Account;
import com.ftn.iss.eventPlanner.model.Notification;
import com.ftn.iss.eventPlanner.repositories.AccountRepository;
import com.ftn.iss.eventPlanner.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String title, String content, Account account) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setDate(LocalDateTime.now());
        notification.setRead(false);

        notification = notificationRepository.save(notification);

        account.getNotifications().add(notification);
        accountRepository.save(account);

        if (!account.isNotificationsSilenced()) {
            Map<String, String> message = new HashMap<>();
            message.put("type", "NOTIFICATION");
            message.put("title", notification.getTitle());
            message.put("content", notification.getContent());
            message.put("id", notification.getId().toString());
            message.put("date", notification.getDate().toString());
            message.put("toId", String.valueOf(account.getId()));

            messagingTemplate.convertAndSend(
                    "/socket-publisher/" + account.getId(),
                    message
            );
        }
    }

    public Page<Notification> getAccountNotifications(Account account, Pageable pageable) {
        Set<Notification> allNotifications = account.getNotifications();

        List<Notification> sortedNotifications = new ArrayList<>(allNotifications);
        sortedNotifications.sort((n1, n2) -> n2.getDate().compareTo(n1.getDate()));

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedNotifications.size());

        List<Notification> pageContent = start < end ?
                sortedNotifications.subList(start, end) :
                new ArrayList<>();

        return new PageImpl<>(
                pageContent,
                pageable,
                sortedNotifications.size()
        );
    }

    public void markAsRead(Long notificationId, Account account) {
        notificationRepository.findById(notificationId)
                .ifPresent(notification -> {
                    notification.setRead(true);
                    notificationRepository.save(notification);
                });
    }
    public void markAllAsRead(Account account){
        Set<Notification> allNotifications = account.getNotifications();
        for (Notification notification : allNotifications) {
            markAsRead(notification.getId(), account);
        }
    }

    public void toggleNotifications(Account account, boolean silenced) {
        account.setNotificationsSilenced(silenced);
        accountRepository.save(account);
    }
}

