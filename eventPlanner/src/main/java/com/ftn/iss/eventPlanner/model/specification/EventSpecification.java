package com.ftn.iss.eventPlanner.model.specification;
import com.ftn.iss.eventPlanner.model.EventStats;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import com.ftn.iss.eventPlanner.model.Event;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EventSpecification {

    public static Specification<Event> hasEventTypeId(Integer eventTypeId) {
        return (root, query, criteriaBuilder) ->
                eventTypeId == null ? null : criteriaBuilder.equal(root.get("eventType").get("id"), eventTypeId);
    }

    public static Specification<Event> hasLocation(String location) {
        return (root, query, criteriaBuilder) ->
                location == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("location").get("city")), "%" + location.toLowerCase() + "%");
    }

    public static Specification<Event> maxParticipants(Integer maxParticipants) {
        return (root, query, criteriaBuilder) ->
                maxParticipants == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("maxParticipants"), maxParticipants);
    }


    public static Specification<Event> betweenDates(String startDateStr, String endDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        LocalDate startDate = startDateStr != null ? LocalDate.parse(startDateStr, formatter) : null;
        LocalDate endDate = endDateStr != null ? LocalDate.parse(endDateStr, formatter) : null;

        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) {
                return null;
            }
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("date"), startDate, endDate);
            }
            return startDate != null ? criteriaBuilder.greaterThanOrEqualTo(root.get("date"), startDate) :
                    criteriaBuilder.lessThanOrEqualTo(root.get("date"), endDate);
        };
    }

    public static Specification<Event> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Event> minAverageRating(Double minRating) {
        return (root, query, criteriaBuilder) -> {
            if (minRating == null) {
                return null;
            }
            Join<Event, EventStats> statsJoin = root.join("stats", JoinType.LEFT);
            return criteriaBuilder.greaterThanOrEqualTo(statsJoin.get("averageRating"), minRating);
        };
    }

}
