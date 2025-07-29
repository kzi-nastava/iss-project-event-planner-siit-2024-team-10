package com.ftn.iss.eventPlanner.dto.calendaritem;

import com.ftn.iss.eventPlanner.model.CalendarItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetCalendarItemDTO {
    private String title;
    private int eventId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private CalendarItemType type;
}
