package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.GetNotificationDTO;
import com.ftn.iss.eventPlanner.dto.PagedResponse;
import com.ftn.iss.eventPlanner.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER','PROVIDER','AUTHENTICATED_USER','ADMIN')")
    @GetMapping(value = "/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedResponse<GetNotificationDTO>> getNotifications(
            Pageable pageable,
            @PathVariable int accountId) {
        PagedResponse<GetNotificationDTO> response = notificationService.getAccountNotifications(pageable, accountId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER','PROVIDER','AUTHENTICATED_USER','ADMIN')")
    @PutMapping(value = "/read/{notificationId}")
    public ResponseEntity<?> markAsRead(@PathVariable Integer notificationId) {
        notificationService.markAsRead(notificationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER','PROVIDER','AUTHENTICATED_USER','ADMIN')")
    @PutMapping("/{accountId}/read-all")
    public ResponseEntity<?> readAll(@PathVariable Integer accountId) {
        notificationService.markAllAsRead(accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER','PROVIDER','AUTHENTICATED_USER','ADMIN')")
    @GetMapping("{accountId}/toggle")
    public ResponseEntity<Boolean> isNotificationSilenced(
            @PathVariable Integer accountId) {
        Boolean response = notificationService.isNotificationsSilenced(accountId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER','PROVIDER','AUTHENTICATED_USER','ADMIN')")
    @PutMapping("{accountId}/change-toggle")
    public ResponseEntity<?> toggleNotifications(
            @PathVariable Integer accountId) {
        notificationService.toggleNotifications(accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
