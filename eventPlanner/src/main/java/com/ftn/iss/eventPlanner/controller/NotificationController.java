package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.GetNotificationDTO;
import com.ftn.iss.eventPlanner.dto.PagedResponse;
import com.ftn.iss.eventPlanner.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<PagedResponse<GetNotificationDTO>> getNotifications(
            Pageable pageable,
            @RequestParam int accountId) {
        PagedResponse<GetNotificationDTO> response = notificationService.getAccountNotifications(pageable, accountId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Integer id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountId}/read-all")
    public ResponseEntity<Void> readAll(@PathVariable Integer accountId) {
        notificationService.markAllAsRead(accountId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("{accountId}/toggle")
    public ResponseEntity<Void> toggleNotifications(
            @PathVariable Integer accountId,
            @RequestParam boolean silenced) {
        notificationService.toggleNotifications(accountId, silenced);
        return ResponseEntity.ok().build();
    }
}
