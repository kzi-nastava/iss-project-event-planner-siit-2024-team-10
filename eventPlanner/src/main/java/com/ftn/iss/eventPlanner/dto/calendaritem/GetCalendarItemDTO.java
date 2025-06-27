package com.ftn.iss.eventPlanner.dto.calendaritem;

import com.ftn.iss.eventPlanner.model.CalendarItemType;

import java.time.LocalDateTime;

public class GetCalendarItemDTO {
    private String title;
    private int eventId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private CalendarItemType type;

    public GetCalendarItemDTO() {
    }

    public GetCalendarItemDTO(String title, int eventId, LocalDateTime startTime, LocalDateTime endTime, CalendarItemType type) {
        this.title = title;
        this.eventId = eventId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public CalendarItemType getType() {
        return type;
    }

    public void setType(CalendarItemType type) {
        this.type = type;
    }
}
