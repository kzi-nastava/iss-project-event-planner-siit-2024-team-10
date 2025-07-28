package com.ftn.iss.eventPlanner.service;

import com.ftn.iss.eventPlanner.dto.reservation.CreateReservationDTO;
import com.ftn.iss.eventPlanner.dto.reservation.CreatedReservationDTO;
import com.ftn.iss.eventPlanner.exception.ServiceUnavailableException;
import com.ftn.iss.eventPlanner.model.*;
import com.ftn.iss.eventPlanner.repositories.EventRepository;
import com.ftn.iss.eventPlanner.repositories.ReservationRepository;
import com.ftn.iss.eventPlanner.repositories.ServiceRepository;
import com.ftn.iss.eventPlanner.services.BudgetItemService;
import com.ftn.iss.eventPlanner.services.EmailService;
import com.ftn.iss.eventPlanner.services.ReservationService;
import com.ftn.iss.eventPlanner.services.ScheduledNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private EmailService emailService;

    @Mock
    private ScheduledNotificationService scheduledNotificationService;

    @Mock
    private BudgetItemService budgetItemService;

    @Spy
    @InjectMocks
    private ReservationService reservationService;

    private static final int VALID_EVENT_ID = 1;
    private static final int VALID_SERVICE_ID = 2;
    private static final int INVALID_EVENT_ID = 1000;
    private static final int INVALID_SERVICE_ID = 2000;

    private Event event;
    private Service service;
    private Reservation reservation;
    private CreateReservationDTO createReservationDTO;
    private CreatedReservationDTO createdReservationDTO;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setDate(LocalDate.now().plusDays(50));
        event.setId(VALID_EVENT_ID);

        Account organizerAccount = new Account();
        organizerAccount.setId(1);
        Organizer organizer = new Organizer();
        organizer.setAccount(organizerAccount);
        event.setOrganizer(organizer);

        Account providerAccount = new Account();
        providerAccount.setId(2);
        Provider provider = new Provider();
        provider.setAccount(providerAccount);

        service = new Service();
        service.setId(VALID_SERVICE_ID);

        ServiceDetails serviceDetails = new ServiceDetails();
        serviceDetails.setAvailable(true);
        serviceDetails.setAutoConfirm(true);

        service.setCurrentDetails(serviceDetails);
        service.setProvider(provider);

        reservation = new Reservation();
        reservation.setId(1);
        reservation.setStartTime(LocalTime.of(10, 0));
        reservation.setEndTime(LocalTime.of(11, 0));
        reservation.setEvent(event);
        reservation.setService(service);

        createReservationDTO = new CreateReservationDTO();
        createReservationDTO.setStartTime(LocalTime.of(10, 0));
        createReservationDTO.setEndTime(LocalTime.of(11, 0));
        createReservationDTO.setEvent(VALID_EVENT_ID);
        createReservationDTO.setService(VALID_SERVICE_ID);

        createdReservationDTO = new CreatedReservationDTO();
    }

    @Test
    void create_WithValidInput_SavesReservation() {
        setupAvailableService(true);

        event.setDate(LocalDate.now().plusDays(10));
        createReservationDTO.setStartTime(LocalTime.of(10, 0));
        createReservationDTO.setEndTime(LocalTime.of(11, 0));

        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(serviceRepository.findById(VALID_SERVICE_ID)).thenReturn(Optional.of(service));
        when(reservationRepository.findAll()).thenReturn(List.of());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(modelMapper.map(reservation, CreatedReservationDTO.class)).thenReturn(createdReservationDTO);

        CreatedReservationDTO result = reservationService.create(createReservationDTO);

        assertThat(result).isNotNull();
        verify(eventRepository).findById(VALID_EVENT_ID);
        verify(serviceRepository).findById(VALID_SERVICE_ID);
        verify(reservationRepository).save(any(Reservation.class));
        verify(modelMapper).map(reservation, CreatedReservationDTO.class);
    }

    @Test
    void create_WithValidInputWhenEventIsWithinADay_SavesReservation() {
        ServiceDetails details = new ServiceDetails();
        details.setAvailable(true);
        details.setReservationPeriod(1);
        details.setMinDuration(1);
        details.setMaxDuration(4);
        service.setCurrentDetails(details);

        event.setDate(LocalDate.now());
        createReservationDTO.setStartTime(LocalTime.of(22, 59));
        createReservationDTO.setEndTime(LocalTime.of(23,59));

        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(serviceRepository.findById(VALID_SERVICE_ID)).thenReturn(Optional.of(service));
        when(reservationRepository.findAll()).thenReturn(List.of());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(modelMapper.map(reservation, CreatedReservationDTO.class)).thenReturn(createdReservationDTO);

        CreatedReservationDTO result = reservationService.create(createReservationDTO);

        assertThat(result).isNotNull();
        verify(eventRepository).findById(VALID_EVENT_ID);
        verify(serviceRepository).findById(VALID_SERVICE_ID);
        verify(reservationRepository).save(any(Reservation.class));
        verify(modelMapper).map(reservation, CreatedReservationDTO.class);
    }

    @Test
    void create_WhenAutoConfirmIsTrue_SetsStatusToAcceptedAndBuysBudget() {
        setupAvailableService(true);

        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(serviceRepository.findById(VALID_SERVICE_ID)).thenReturn(Optional.of(service));
        when(reservationRepository.findAll()).thenReturn(List.of());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(modelMapper.map(reservation, CreatedReservationDTO.class)).thenReturn(createdReservationDTO);

        CreatedReservationDTO result = reservationService.create(createReservationDTO);

        assertThat(result).isNotNull();
        verify(reservationRepository).save(argThat(res -> res.getStatus() == Status.ACCEPTED));
        verify(budgetItemService).buy(event.getId(), service.getId());
        verify(emailService, times(2)).sendSimpleEmail(any(EmailDetails.class));
        verify(scheduledNotificationService).scheduleReservationReminder(any(Reservation.class));
    }

    @Test
    void create_WhenAutoConfirmIsFalse_SetsStatusToPending() {
        setupAvailableService(false);

        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(serviceRepository.findById(VALID_SERVICE_ID)).thenReturn(Optional.of(service));
        when(reservationRepository.findAll()).thenReturn(List.of());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(modelMapper.map(reservation, CreatedReservationDTO.class)).thenReturn(createdReservationDTO);

        CreatedReservationDTO result = reservationService.create(createReservationDTO);

        assertThat(result).isNotNull();
        verify(reservationRepository).save(argThat(res -> res.getStatus() == Status.PENDING));
        verify(budgetItemService, never()).buy(anyInt(), anyInt());
        verify(emailService, times(2)).sendSimpleEmail(any(EmailDetails.class));
        verify(scheduledNotificationService).scheduleReservationReminder(any(Reservation.class));
    }

    @Test
    void create_WhenEventNotFound_ThrowsIllegalArgumentException() {
        createReservationDTO.setEvent(INVALID_EVENT_ID);
        when(eventRepository.findById(INVALID_EVENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.create(createReservationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event with ID "+ INVALID_EVENT_ID +" not found");

        verify(eventRepository).findById(INVALID_EVENT_ID);
        verifyNoInteractions(serviceRepository, reservationRepository, modelMapper);
    }

    @Test
    void create_WhenServiceNotFound_ThrowsIllegalArgumentException() {
        createReservationDTO.setService(INVALID_SERVICE_ID);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(serviceRepository.findById(INVALID_SERVICE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.create(createReservationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Service with ID "+ INVALID_SERVICE_ID+ " not found");

        verify(eventRepository).findById(VALID_EVENT_ID);
        verify(serviceRepository).findById(INVALID_SERVICE_ID);
        verifyNoInteractions(reservationRepository, modelMapper);
    }

    @Test
    void create_WhenServiceNotAvailable_ThrowsServiceUnavailableException() {
        service.setCurrentDetails(new ServiceDetails());
        service.getCurrentDetails().setAvailable(false);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(serviceRepository.findById(VALID_SERVICE_ID)).thenReturn(Optional.of(service));

        assertThatThrownBy(() -> reservationService.create(createReservationDTO))
                .isInstanceOf(ServiceUnavailableException.class)
                .hasMessage("Service is not available at selected time.");
    }

    @Test
    void create_WhenServiceAlreadyReservedForEvent_ThrowsIllegalArgumentException() {
        Reservation existingReservation = new Reservation();
        existingReservation.setEvent(event);
        existingReservation.setService(service);

        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(serviceRepository.findById(VALID_SERVICE_ID)).thenReturn(Optional.of(service));
        when(reservationRepository.findAll()).thenReturn(List.of(existingReservation));
        
        assertThatThrownBy(() -> reservationService.create(createReservationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("You've already made a reservation for selected event.");
    }

    @Test
    void create_WhenDateIsNotWithinReservationPeriod_ThrowsIllegalArgumentException() {
        event.setDate(LocalDate.now().plusDays(2));
        setupAvailableService(true);
        createReservationDTO.setStartTime(LocalTime.of(10, 0));
        createReservationDTO.setEndTime(LocalTime.of(11, 0));
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(serviceRepository.findById(VALID_SERVICE_ID)).thenReturn(Optional.of(service));

        assertThatThrownBy(() -> reservationService.create(createReservationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reservation must be made within the reservation period.");
    }

    @Test
    void create_WhenEventHasPassed_ThrowsIllegalArgumentException() {
        event.setDate(LocalDate.now().minusDays(2));
        setupAvailableService(true);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(serviceRepository.findById(VALID_SERVICE_ID)).thenReturn(Optional.of(service));

        assertThatThrownBy(() -> reservationService.create(createReservationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reservation must be made within the reservation period.");
    }

    @Test
    void create_WhenStartTimeIsNull_ThrowsIllegalArgumentException() {
        createReservationDTO.setStartTime(null);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(serviceRepository.findById(VALID_SERVICE_ID)).thenReturn(Optional.of(service));

        assertThatThrownBy(() -> reservationService.create(createReservationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Start time and end time must be provided");
    }

    @Test
    void create_WhenEndTimeIsNull_ThrowsIllegalArgumentException() {
        createReservationDTO.setEndTime(null);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(serviceRepository.findById(VALID_SERVICE_ID)).thenReturn(Optional.of(service));

        assertThatThrownBy(() -> reservationService.create(createReservationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Start time and end time must be provided");
    }

    @Test
    void create_WhenEndBeforeStart_ThrowsIllegalArgumentException() {
        createReservationDTO.setStartTime(LocalTime.of(12, 0));
        createReservationDTO.setEndTime(LocalTime.of(10, 0));
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(serviceRepository.findById(VALID_SERVICE_ID)).thenReturn(Optional.of(service));

        assertThatThrownBy(() -> reservationService.create(createReservationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("End time must be after start time");
    }

    @Test
    void create_WhenStartEqualsEnd_ThrowsIllegalArgumentException() {
        createReservationDTO.setStartTime(LocalTime.of(12, 0));
        createReservationDTO.setEndTime(LocalTime.of(12, 0));
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(serviceRepository.findById(VALID_SERVICE_ID)).thenReturn(Optional.of(service));

        assertThatThrownBy(() -> reservationService.create(createReservationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("End time must be after start time");
    }

    @Test
    void create_WhenDurationTooShort_ThrowsIllegalArgumentException() {
        service.getCurrentDetails().setMinDuration(2);
        service.getCurrentDetails().setMaxDuration(4);
        createReservationDTO.setStartTime(LocalTime.of(10, 0));
        createReservationDTO.setEndTime(LocalTime.of(11, 0));
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(serviceRepository.findById(VALID_SERVICE_ID)).thenReturn(Optional.of(service));

        assertThatThrownBy(() -> reservationService.create(createReservationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Selected duration is not within the service's duration limits");
    }

    @Test
    void create_WhenDurationTooLong_ThrowsIllegalArgumentException() {
        service.getCurrentDetails().setMinDuration(1);
        service.getCurrentDetails().setMaxDuration(2);
        createReservationDTO.setStartTime(LocalTime.of(10, 0));
        createReservationDTO.setEndTime(LocalTime.of(13, 0));
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(serviceRepository.findById(VALID_SERVICE_ID)).thenReturn(Optional.of(service));

        assertThatThrownBy(() -> reservationService.create(createReservationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Selected duration is not within the service's duration limits");
    }

    @Test
    void create_WhenDurationMinuteTooShort_ThrowsIllegalArgumentException() {
        service.getCurrentDetails().setMinDuration(1);
        service.getCurrentDetails().setMaxDuration(2);
        createReservationDTO.setStartTime(LocalTime.of(10, 0));
        createReservationDTO.setEndTime(LocalTime.of(10, 59));
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(serviceRepository.findById(VALID_SERVICE_ID)).thenReturn(Optional.of(service));

        assertThatThrownBy(() -> reservationService.create(createReservationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Selected duration is not within the service's duration limits");
    }

    @Test
    void create_WhenNewReservationStartsExactlyWhenExistingEnds_ShouldNotOverlap() {
        Reservation existingReservation = new Reservation();
        Event event2 = new Event();
        event2.setDate(LocalDate.now().plusDays(50));
        existingReservation.setEvent(event2);
        existingReservation.setService(service);
        existingReservation.setStartTime(LocalTime.of(10, 0));
        existingReservation.setEndTime(LocalTime.of(12, 0));

        createReservationDTO.setStartTime(LocalTime.of(12, 0));
        createReservationDTO.setEndTime(LocalTime.of(13, 30));

        setupAvailableService(true);

        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(serviceRepository.findById(VALID_SERVICE_ID)).thenReturn(Optional.of(service));
        when(reservationRepository.findAll()).thenReturn(List.of(existingReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(modelMapper.map(reservation, CreatedReservationDTO.class)).thenReturn(createdReservationDTO);

        CreatedReservationDTO result = reservationService.create(createReservationDTO);

        assertThat(result).isNotNull();
        verify(eventRepository).findById(VALID_EVENT_ID);
        verify(serviceRepository).findById(VALID_SERVICE_ID);
        verify(reservationRepository).save(argThat(res -> res.getStatus() == Status.ACCEPTED));
        verify(modelMapper).map(reservation, CreatedReservationDTO.class);
        verify(budgetItemService).buy(event.getId(), service.getId());
        verify(emailService, times(2)).sendSimpleEmail(any(EmailDetails.class));
        verify(scheduledNotificationService).scheduleReservationReminder(any(Reservation.class));
    }

    @Test
    void create_WhenAutoConfirmIsTrue_SendsCorrectEmails() {
        setupAvailableService(true);

        service.getCurrentDetails().setName("Test Service");
        event.setName("Test Event");
        event.getOrganizer().getAccount().setEmail("organizer@example.com");
        service.getProvider().getAccount().setEmail("provider@example.com");
        event.getOrganizer().setFirstName("John");
        event.getOrganizer().setLastName("Doe");


        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(serviceRepository.findById(VALID_SERVICE_ID)).thenReturn(Optional.of(service));
        when(reservationRepository.findAll()).thenReturn(List.of());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(modelMapper.map(reservation, CreatedReservationDTO.class)).thenReturn(createdReservationDTO);

        reservationService.create(createReservationDTO);

        verify(emailService).sendSimpleEmail(argThat(email ->
                email.getRecipient().equals("organizer@example.com") &&
                        email.getSubject().equals("Reservation Confirmation") &&
                        email.getMsgBody().equals("You've successfully reserved Test Service for Test Event!")
        ));

        verify(emailService).sendSimpleEmail(argThat(email ->
                email.getRecipient().equals("provider@example.com") &&
                        email.getSubject().equals("Your Service Has Gotten A Reservation") &&
                        email.getMsgBody().equals("Your service Test Service has been reserved for Test Event by John Doe and has been automatically accepted.")
        ));
    }

    @Test
    void create_WhenAutoConfirmIsFalse_SendsCorrectEmails() {
        setupAvailableService(false);

        service.getCurrentDetails().setName("Test Service");
        event.setName("Test Event");
        event.getOrganizer().getAccount().setEmail("organizer@example.com");
        service.getProvider().getAccount().setEmail("provider@example.com");
        event.getOrganizer().setFirstName("John");
        event.getOrganizer().setLastName("Doe");

        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(serviceRepository.findById(VALID_SERVICE_ID)).thenReturn(Optional.of(service));
        when(reservationRepository.findAll()).thenReturn(List.of());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(modelMapper.map(reservation, CreatedReservationDTO.class)).thenReturn(createdReservationDTO);

        reservationService.create(createReservationDTO);

        verify(emailService).sendSimpleEmail(argThat(email ->
                email.getRecipient().equals("organizer@example.com") &&
                        email.getSubject().equals("Reservation Confirmation") &&
                        email.getMsgBody().equals("Reservation for service Test Service for your event Test Event is pending. You will get a confirmation when it gets accepted/denied.")
        ));

        verify(emailService).sendSimpleEmail(argThat(email ->
                email.getRecipient().equals("provider@example.com") &&
                        email.getSubject().equals("Your Service Has Gotten A Reservation") &&
                        email.getMsgBody().equals("Your service Test Service has been reserved for Test Event by John Doe and has been added to pending reservation where you can confirm/deny it.")
        ));
    }
    @Test
    void create_WhenReservationIsSuccessful_SchedulesReminder() {
        setupAvailableService(true);

        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(serviceRepository.findById(VALID_SERVICE_ID)).thenReturn(Optional.of(service));
        when(reservationRepository.findAll()).thenReturn(List.of());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(modelMapper.map(reservation, CreatedReservationDTO.class)).thenReturn(createdReservationDTO);

        reservationService.create(createReservationDTO);

        verify(scheduledNotificationService).scheduleReservationReminder(any(Reservation.class));
    }

    @ParameterizedTest
    @MethodSource("overlappingTimeScenarios")
    void create_WhenNewReservationOverlapsWithExisting_ThrowsIllegalArgumentException(LocalTime newStart, LocalTime newEnd) {
        Reservation existingReservation = new Reservation();
        Event event2 = new Event();
        event2.setDate(LocalDate.now().plusDays(50));
        existingReservation.setEvent(event2);
        existingReservation.setService(service);
        existingReservation.setStartTime(LocalTime.of(10, 0));
        existingReservation.setEndTime(LocalTime.of(12, 0));

        createReservationDTO.setStartTime(newStart);
        createReservationDTO.setEndTime(newEnd);

        setupAvailableService(true);
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(serviceRepository.findById(VALID_SERVICE_ID)).thenReturn(Optional.of(service));
        when(reservationRepository.findAll()).thenReturn(List.of(existingReservation));

        assertThatThrownBy(() -> reservationService.create(createReservationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Service not available at selected time.");

        verify(reservationRepository, never()).save(any());
    }

    // ========================= HELPER METHODS =========================

    private static Stream<Arguments> overlappingTimeScenarios() {
        return Stream.of(
                Arguments.of(LocalTime.of(10, 30), LocalTime.of(11, 30)), // within existing
                Arguments.of(LocalTime.of(10, 0), LocalTime.of(12, 0)),   // same as existing
                Arguments.of(LocalTime.of(11, 30), LocalTime.of(13, 0)),  // partial overlap
                Arguments.of(LocalTime.of(9, 0), LocalTime.of(10, 30)),   // starts before, ends within existing
                Arguments.of(LocalTime.of(10, 30), LocalTime.of(12, 30))  // starts during existing, ends after
        );
    }

    private void setupAvailableService(boolean autoConfirm) {
        ServiceDetails details = new ServiceDetails();
        details.setAvailable(true);
        details.setAutoConfirm(autoConfirm);
        details.setReservationPeriod(50);
        details.setMinDuration(1);
        details.setMaxDuration(4);
        service.setCurrentDetails(details);
    }
}