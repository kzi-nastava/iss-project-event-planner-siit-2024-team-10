package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.GetNotificationDTO;
import com.ftn.iss.eventPlanner.dto.PagedResponse;
import com.ftn.iss.eventPlanner.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{accountId}")
    public ResponseEntity<PagedResponse<GetNotificationDTO>> getNotifications(
            Pageable pageable,
            @PathVariable int accountId) {
        PagedResponse<GetNotificationDTO> response = notificationService.getAccountNotifications(pageable, accountId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Integer id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER','PROVIDER')")
    @PutMapping("/{accountId}/read-all")
    public ResponseEntity<Void> readAll(@PathVariable Integer accountId) {
        notificationService.markAllAsRead(accountId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER','PROVIDER')")
    @PutMapping("{accountId}/toggle")
    public ResponseEntity<Void> toggleNotifications(
            @PathVariable Integer accountId,
            @RequestParam boolean silenced) {
        notificationService.toggleNotifications(accountId, silenced);
        return ResponseEntity.ok().build();
    }
}
