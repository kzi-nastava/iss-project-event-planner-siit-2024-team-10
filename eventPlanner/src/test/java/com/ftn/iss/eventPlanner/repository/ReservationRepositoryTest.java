package com.ftn.iss.eventPlanner.repository;

import com.ftn.iss.eventPlanner.model.*;
import com.ftn.iss.eventPlanner.repositories.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Event event1;
    private Event event2;

    @BeforeEach
    void setUp() {
        Location location = new Location();
        location.setCity("City");
        location.setCountry("Country");
        location.setStreet("Street");
        location.setHouseNumber("1");
        location = entityManager.persistAndFlush(location);

        Account account = new Account();
        account.setEmail("user@example.com");
        account.setPassword("password");
        account.setRole(Role.EVENT_ORGANIZER);
        account.setStatus(AccountStatus.ACTIVE);
        account.setNotificationsSilenced(false);
        account.setLastPasswordResetDate(new Timestamp(System.currentTimeMillis()));
        account = entityManager.persistAndFlush(account);

        Organizer organizer = new Organizer();
        organizer.setFirstName("First");
        organizer.setLastName("Last");
        organizer.setPhoneNumber("123456789");
        organizer.setAccount(account);
        organizer.setLocation(location);
        organizer = entityManager.persistAndFlush(organizer);

        EventType eventType = new EventType();
        eventType.setName("Type");
        eventType.setDescription("Description");
        eventType.setActive(true);
        eventType = entityManager.persistAndFlush(eventType);

        event1 = new Event();
        event1.setName("Event 1");
        event1.setDescription("Description 1");
        event1.setDate(LocalDate.now().plusDays(10));
        event1.setOpen(true);
        event1.setDeleted(false);
        event1.setMaxParticipants(100);
        event1.setDateCreated(LocalDateTime.now());
        event1.setOrganizer(organizer);
        event1.setEventType(eventType);
        event1.setLocation(location);
        event1 = entityManager.persistAndFlush(event1);

        event2 = new Event();
        event2.setName("Event 2");
        event2.setDescription("Description 2");
        event2.setDate(LocalDate.now().plusDays(20));
        event2.setOpen(true);
        event2.setDeleted(false);
        event2.setMaxParticipants(50);
        event2.setDateCreated(LocalDateTime.now());
        event2.setOrganizer(organizer);
        event2.setEventType(eventType);
        event2.setLocation(location);
        event2 = entityManager.persistAndFlush(event2);

        Reservation reservation1 = new Reservation();
        reservation1.setEvent(event1);
        reservation1.setStartTime(LocalTime.of(10, 0));
        reservation1.setEndTime(LocalTime.of(12, 0));
        reservation1.setStatus(Status.ACCEPTED);
        reservation1.setTimestamp(LocalDateTime.now());
        reservation1 = entityManager.persistAndFlush(reservation1);

        Reservation reservation2 = new Reservation();
        reservation2.setEvent(event1);
        reservation2.setStartTime(LocalTime.of(13, 0));
        reservation2.setEndTime(LocalTime.of(14, 0));
        reservation2.setStatus(Status.ACCEPTED);
        reservation2.setTimestamp(LocalDateTime.now());
        reservation2 = entityManager.persistAndFlush(reservation2);
    }

    @Test
    @DisplayName("Should return reservations for a given event ID")
    public void shouldReturnReservationsByEventId() {
        Collection<Reservation> reservations = reservationRepository.findByEventId(event1.getId());

        assertThat(reservations).hasSize(2);
        assertThat(reservations).allMatch(r -> r.getEvent().getId() == event1.getId());
    }

    @Test
    @DisplayName("Should return empty list when event has no reservations")
    public void shouldReturnEmptyWhenEventHasNoReservations() {
        Collection<Reservation> reservations = reservationRepository.findByEventId(event2.getId());

        assertThat(reservations).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list for non-existing event")
    public void shouldReturnEmptyForNonExistingEvent() {
        int nonExistingEventId = 0;

        Collection<Reservation> reservations = reservationRepository.findByEventId(nonExistingEventId);

        assertThat(reservations).isEmpty();
    }

}
