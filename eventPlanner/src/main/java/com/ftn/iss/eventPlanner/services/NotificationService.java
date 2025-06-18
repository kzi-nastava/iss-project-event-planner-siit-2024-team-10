package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.controller.AuthenticationController;
import com.ftn.iss.eventPlanner.dto.GetNotificationDTO;
import com.ftn.iss.eventPlanner.dto.PagedResponse;
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
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private NotificationRepository notificationRepository;

//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//    @Autowired
//    private AuthenticationController authenticationController;

//    public void sendNotification(String title, String content, Integer recipient) {
//        Notification notification = new Notification();
//        notification.setTitle(title);
//        notification.setContent(content);
//        notification.setDate(LocalDateTime.now());
//        notification.setRead(false);
//        Account recipientAccount = accountRepository.findById(recipient)
//                .orElseThrow(() -> new NotFoundException("Account not found"));
//
//        recipientAccount.getNotifications().add(notification);
//
//        notification = notificationRepository.save(notification);
//        accountRepository.save(recipientAccount);
//
//        if (!recipientAccount.isNotificationsSilenced()) {
//            Map<String, String> message = new HashMap<>();
//            message.put("type", "NOTIFICATION");
//            message.put("title", notification.getTitle());
//            message.put("content", notification.getContent());
//            message.put("id", notification.getId().toString());
//            message.put("date", notification.getDate().toString());
//            message.put("toId", String.valueOf(recipient));
//
//            messagingTemplate.convertAndSend(
//                    "/socket-publisher/" + recipient,
//                    message
//            );
//        }
//    }

    public PagedResponse<GetNotificationDTO> getAccountNotifications(Pageable pageable, int accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found with ID: " + accountId));
        Set<Notification> notifications = account.getNotifications();

        List<Notification> sortedNotifications = sortNotificationsByDateDesc(notifications);

        Page<Notification> pagedNotifications = applyPagination(sortedNotifications, pageable);

        List<GetNotificationDTO> notificationDTOs = mapNotificationsToDTOs(pagedNotifications.getContent());

        return new PagedResponse<>(
                notificationDTOs,
                pagedNotifications.getTotalPages(),
                pagedNotifications.getTotalElements()
        );
    }

    private List<Notification> sortNotificationsByDateDesc(Set<Notification> notifications) {
        return notifications.stream()
                .sorted(Comparator.comparing(Notification::getDate).reversed())
                .collect(Collectors.toList());
    }

    private Page<Notification> applyPagination(List<Notification> sortedNotifications, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedNotifications.size());

        List<Notification> pageContent = (start < sortedNotifications.size() && start < end) ?
                sortedNotifications.subList(start, end) :
                new ArrayList<>();

        return new PageImpl<>(pageContent, pageable, sortedNotifications.size());
    }

    private List<GetNotificationDTO> mapNotificationsToDTOs(List<Notification> notifications) {
        return notifications.stream()
                .map(this::mapToNotificationDTO)
                .collect(Collectors.toList());
    }

    private GetNotificationDTO mapToNotificationDTO(Notification notification) {
        GetNotificationDTO dto = new GetNotificationDTO();

        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setDate(notification.getDate());
        dto.setRead(notification.isRead());
        dto.setContent(notification.getContent());
        return dto;
    }

    @Transactional
    public void markAsRead(Integer notificationId) {
        notificationRepository.findById(notificationId)
                .ifPresent(notification -> {
                    notification.setRead(true);
                    notificationRepository.save(notification);
                });
    }

    public void markAllAsRead(Integer accountId){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        Set<Notification> allNotifications = account.getNotifications();
        for (Notification notification : allNotifications) {
            markAsRead(notification.getId());
        }
    }

    public void toggleNotifications(Integer accountId, boolean silenced) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        account.setNotificationsSilenced(silenced);
        accountRepository.save(account);
    }
}

