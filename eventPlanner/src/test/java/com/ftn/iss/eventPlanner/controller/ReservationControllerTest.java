package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.event.GetEventDTO;
import com.ftn.iss.eventPlanner.dto.reservation.CreateReservationDTO;
import com.ftn.iss.eventPlanner.dto.reservation.CreatedReservationDTO;
import com.ftn.iss.eventPlanner.dto.reservation.GetReservationDTO;
import com.ftn.iss.eventPlanner.model.Account;
import com.ftn.iss.eventPlanner.model.Role;
import com.ftn.iss.eventPlanner.util.TokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
public class ReservationControllerTest {

    private static final String BASE = "/api/reservations";
    private static final int ORGANIZER_ID = 1;
    private static final int PROVIDER_ID = 5;
    private static final int EVENT_ID = 4; // date 2025-10-15
    private static final int EVENT_ID_FOR_INVALID_RESERVATIONS = 1;
    private static final int PASSED_EVENT_ID = 3;
    private static final int SERVICE_ID = 19; // duration 2-4h
    private static final int EXISTING_RESERVATION_ID = 1;
    private static final int NON_EXISTENT_ID = 999;
    private static final int EXISTING_PENDING_RESERVATION_ACCEPT_ID = 3;
    private static final int EXISTING_PENDING_RESERVATION_REJECT_ID = 4;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TokenUtils tokenUtils;

    private String organizerToken;
    private String providerToken;
    private String userToken;

    @BeforeEach
    public void setUp() {
        Account organizer = new Account();
        organizer.setId(ORGANIZER_ID);
        organizer.setEmail("organizer@mail.com");
        organizer.setRole(Role.EVENT_ORGANIZER);
        organizerToken = tokenUtils.generateToken(organizer);

        Account provider = new Account();
        provider.setId(PROVIDER_ID);
        provider.setEmail("provider@mail.com");
        provider.setRole(Role.PROVIDER);
        providerToken = tokenUtils.generateToken(provider);

        Account user = new Account();
        user.setId(3);
        user.setEmail("auth@mail.com");
        user.setRole(Role.AUTHENTICATED_USER);
        userToken = tokenUtils.generateToken(user);
    }


    private HttpHeaders getHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }

    @Test
    @DisplayName("Create Reservation - Success")
    public void createReservation_Success() {
        CreateReservationDTO dto = new CreateReservationDTO();
        dto.setStartTime(LocalTime.of(12, 0));
        dto.setEndTime(LocalTime.of(15, 0));
        dto.setEvent(EVENT_ID);
        dto.setService(SERVICE_ID);

        HttpEntity<CreateReservationDTO> request = new HttpEntity<>(dto, getHeaders(organizerToken));

        ResponseEntity<CreatedReservationDTO> response = restTemplate.exchange(
                BASE,
                HttpMethod.POST,
                request,
                CreatedReservationDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Create Reservation - Unauthorized without token")
    public void createReservation_Unauthorized() {
        CreateReservationDTO dto = new CreateReservationDTO();
        dto.setStartTime(LocalTime.of(12, 0));
        dto.setEndTime(LocalTime.of(13, 0));
        dto.setEvent(EVENT_ID);
        dto.setService(SERVICE_ID);

        HttpEntity<CreateReservationDTO> request = new HttpEntity<>(dto);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE,
                HttpMethod.POST,
                request,
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Create Reservation - Fail: End time before start time")
    public void createReservation_EndBeforeStart() {
        CreateReservationDTO dto = new CreateReservationDTO();
        dto.setStartTime(LocalTime.of(15, 0));
        dto.setEndTime(LocalTime.of(14, 0));
        dto.setEvent(EVENT_ID_FOR_INVALID_RESERVATIONS);
        dto.setService(SERVICE_ID);

        HttpEntity<CreateReservationDTO> request = new HttpEntity<>(dto, getHeaders(organizerToken));

        ResponseEntity<String> response = restTemplate.exchange(
                BASE,
                HttpMethod.POST,
                request,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("End time must be after start time"));
    }

    @Test
    @DisplayName("Create Reservation - Fail: Duration shorter than min duration")
    public void createReservation_DurationTooShort() {
        CreateReservationDTO dto = new CreateReservationDTO();
        dto.setStartTime(LocalTime.of(12, 0));
        dto.setEndTime(LocalTime.of(12, 30));
        dto.setEvent(EVENT_ID_FOR_INVALID_RESERVATIONS);
        dto.setService(SERVICE_ID);

        HttpEntity<CreateReservationDTO> request = new HttpEntity<>(dto, getHeaders(organizerToken));

        ResponseEntity<String> response = restTemplate.exchange(
                BASE,
                HttpMethod.POST,
                request,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Selected duration is not within the service's duration limits"));
    }

    @Test
    @DisplayName("Create Reservation - Fail: Reservation too late (outside reservation period)")
    public void createReservation_OutsideReservationPeriod() {
        CreateReservationDTO dto = new CreateReservationDTO();
        dto.setStartTime(LocalTime.now().plusMinutes(30));
        dto.setEndTime(LocalTime.now().plusHours(3));
        dto.setEvent(PASSED_EVENT_ID);
        dto.setService(SERVICE_ID);

        HttpEntity<CreateReservationDTO> request = new HttpEntity<>(dto, getHeaders(organizerToken));

        ResponseEntity<String> response = restTemplate.exchange(
                BASE,
                HttpMethod.POST,
                request,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Reservation must be made within the reservation period"));
    }

    @Test
    @DisplayName("Create Reservation - Fail: Reservation already made for the service")
    public void createReservation_ReservationAlreadyMade() {
        CreateReservationDTO dto = new CreateReservationDTO();
        dto.setStartTime(LocalTime.of(12, 0));
        dto.setEndTime(LocalTime.of(18, 0));
        dto.setEvent(2);
        dto.setService(11);

        HttpEntity<CreateReservationDTO> request = new HttpEntity<>(dto, getHeaders(organizerToken));

        ResponseEntity<String> response = restTemplate.exchange(
                BASE,
                HttpMethod.POST,
                request,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("You've already made a reservation for selected event."));
    }

    @Test
    @DisplayName("Create Reservation - Fail: Service unavailable at selected time")
    public void createReservation_ServiceUnavailable() {
        CreateReservationDTO dto = new CreateReservationDTO();
        dto.setStartTime(LocalTime.of(12, 0));
        dto.setEndTime(LocalTime.of(18, 0));
        dto.setEvent(2);
        dto.setService(12);

        HttpEntity<CreateReservationDTO> request = new HttpEntity<>(dto, getHeaders(organizerToken));

        ResponseEntity<String> response = restTemplate.exchange(
                BASE,
                HttpMethod.POST,
                request,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Service not available at selected time."));
    }

    @Test
    @DisplayName("Create Reservation - Forbidden for regular user")
    public void createReservation_Forbidden() {
        CreateReservationDTO dto = new CreateReservationDTO();
        dto.setStartTime(LocalTime.of(12, 0));
        dto.setEndTime(LocalTime.of(13, 0));
        dto.setEvent(EVENT_ID);
        dto.setService(SERVICE_ID);

        HttpEntity<CreateReservationDTO> request = new HttpEntity<>(dto, getHeaders(userToken));

        ResponseEntity<String> response = restTemplate.exchange(
                BASE,
                HttpMethod.POST,
                request,
                String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Find Events by Organizer - Success")
    public void findEventsByOrganizer_Success() {
        HttpEntity<Void> request = new HttpEntity<>(getHeaders(organizerToken));

        ResponseEntity<GetEventDTO[]> response = restTemplate.exchange(
                BASE + "/events/" + ORGANIZER_ID,
                HttpMethod.GET,
                request,
                GetEventDTO[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Find Events by Organizer - Unauthorized")
    public void findEventsByOrganizer_Unauthorized() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/events/" + ORGANIZER_ID,
                HttpMethod.GET,
                new HttpEntity<>(null),
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Find Events by Organizer - Forbidden with non organizer role")
    public void findEventsByOrganizer_ForbiddenRole() {
        HttpEntity<Void> request = new HttpEntity<>(getHeaders(providerToken));

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/events/" + ORGANIZER_ID,
                HttpMethod.GET,
                request,
                String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Cancel Reservation - Success")
    public void cancelReservation_Success() {
        HttpEntity<Void> request = new HttpEntity<>(getHeaders(organizerToken));

        ResponseEntity<Void> response = restTemplate.exchange(
                BASE + "/" + EXISTING_RESERVATION_ID,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Cancel Reservation - Unauthorized")
    public void cancelReservation_Unauthorized() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_RESERVATION_ID,
                HttpMethod.DELETE,
                new HttpEntity<>(null),
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Cancel Reservation - Forbidden")
    public void cancelReservation_ForbiddenRole() {
        HttpEntity<Void> request = new HttpEntity<>(getHeaders(providerToken));

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_RESERVATION_ID,
                HttpMethod.DELETE,
                request,
                String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Cancel Reservation - Not Found")
    public void cancelReservation_ReservationNotFound() {
        HttpEntity<Void> request = new HttpEntity<>(getHeaders(organizerToken));

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + NON_EXISTENT_ID,
                HttpMethod.DELETE,
                request,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Get Pending Reservations - Success for provider")
    public void getPendingReservations_Success() {
        HttpEntity<Void> request = new HttpEntity<>(getHeaders(providerToken));

        ResponseEntity<GetReservationDTO[]> response = restTemplate.exchange(
                BASE + "/" + PROVIDER_ID + "/pending",
                HttpMethod.GET,
                request,
                GetReservationDTO[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Get Pending Reservations - Unauthorized (no token)")
    public void getPendingReservations_Unauthorized() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + PROVIDER_ID + "/pending",
                HttpMethod.GET,
                new HttpEntity<>(null),
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Get Pending Reservations - Forbidden for wrong role")
    public void getPendingReservations_Forbidden() {
        HttpEntity<Void> request = new HttpEntity<>(getHeaders(userToken));

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + PROVIDER_ID + "/pending",
                HttpMethod.GET,
                request,
                String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Accept Reservation - Success")
    public void acceptReservation_Success() {
        HttpEntity<Void> request = new HttpEntity<>(getHeaders(providerToken));

        ResponseEntity<Void> response = restTemplate.exchange(
                BASE + "/"+EXISTING_PENDING_RESERVATION_ACCEPT_ID+"/accept",
                HttpMethod.PUT,
                request,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Accept Reservation - Unauthorized")
    public void acceptReservation_Unauthorized() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/"+EXISTING_PENDING_RESERVATION_ACCEPT_ID+"/accept",
                HttpMethod.PUT,
                new HttpEntity<>(null),
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Accept Reservation - Forbidden for wrong role")
    public void acceptReservation_Forbidden() {
        HttpEntity<Void> request = new HttpEntity<>(getHeaders(userToken));

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/"+EXISTING_PENDING_RESERVATION_ACCEPT_ID+"/accept",
                HttpMethod.PUT,
                request,
                String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
    @Test
    @DisplayName("Reject Reservation - Success")
    public void rejectReservation_Success() {
        HttpEntity<Void> request = new HttpEntity<>(getHeaders(providerToken));

        ResponseEntity<Void> response = restTemplate.exchange(
                BASE + "/"+EXISTING_PENDING_RESERVATION_REJECT_ID+"/reject",
                HttpMethod.PUT,
                request,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Reject Reservation - Unauthorized")
    public void rejectReservation_Unauthorized() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/"+EXISTING_PENDING_RESERVATION_REJECT_ID+"/reject",
                HttpMethod.PUT,
                new HttpEntity<>(null),
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Reject Reservation - Forbidden for wrong role")
    public void rejectReservation_Forbidden() {
        HttpEntity<Void> request = new HttpEntity<>(getHeaders(userToken));

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/"+EXISTING_PENDING_RESERVATION_REJECT_ID+"/reject",
                HttpMethod.PUT,
                request,
                String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Accept Reservation - Not Found")
    public void acceptReservation_NotFound() {
        HttpEntity<Void> request = new HttpEntity<>(getHeaders(providerToken));

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + NON_EXISTENT_ID + "/accept",
                HttpMethod.PUT,
                request,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Reject Reservation - Not Found")
    public void rejectReservation_NotFound() {
        HttpEntity<Void> request = new HttpEntity<>(getHeaders(providerToken));

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + NON_EXISTENT_ID + "/reject",
                HttpMethod.PUT,
                request,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}