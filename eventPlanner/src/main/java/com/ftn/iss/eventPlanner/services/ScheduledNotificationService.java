package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.model.Reservation;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class ScheduledNotificationService {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TaskScheduler taskScheduler;

    public void scheduleReservationReminder(Reservation reservation) {
        LocalDateTime reminderTime = LocalDateTime.of(reservation.getEvent().getDate(), reservation.getStartTime()).minusHours(1);
        if (reminderTime.isAfter(LocalDateTime.now())) {
            taskScheduler.schedule(() -> {
                int recipientId = reservation.getEvent().getOrganizer().getAccount().getId();
                String title = "Upcoming Reservation Reminder";
                String content = "Reminder: Your reservation for service \"" + reservation.getService().getCurrentDetails().getName() +
                        "\" at event \"" + reservation.getEvent().getName() + "\" starts in 1 hour.";
                notificationService.sendNotification(recipientId, title, content);
            }, Date.from(reminderTime.atZone(ZoneId.systemDefault()).toInstant()));
        }
    }
}

