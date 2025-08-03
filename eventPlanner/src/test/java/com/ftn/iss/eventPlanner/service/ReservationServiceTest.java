package com.ftn.iss.eventPlanner.service;

import com.ftn.iss.eventPlanner.dto.reservation.CreateReservationDTO;
import com.ftn.iss.eventPlanner.dto.reservation.CreatedReservationDTO;
import com.ftn.iss.eventPlanner.dto.reservation.GetReservationDTO;
import com.ftn.iss.eventPlanner.exception.ServiceUnavailableException;
import com.ftn.iss.eventPlanner.model.*;
import com.ftn.iss.eventPlanner.repositories.EventRepository;
import com.ftn.iss.eventPlanner.repositories.ReservationRepository;
import com.ftn.iss.eventPlanner.repositories.ServiceRepository;
import com.ftn.iss.eventPlanner.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.webjars.NotFoundException;

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
    private NotificationService notificationService;

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
    private static final int VALID_PROVIDER_ID = 2;
    private static final int VALID_RESERVATION_ID = 1;
    private static final int INVALID_RESERVATION_ID = 3000;

    private Event event;
    private Service service;
    private Reservation reservation;
    private CreateReservationDTO createReservationDTO;
    private CreatedReservationDTO createdReservationDTO;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setName("Event 1");
        event.setDate(LocalDate.now().plusDays(50));
        event.setId(VALID_EVENT_ID);

        Account organizerAccount = new Account();
        organizerAccount.setId(1);
        Organizer organizer = new Organizer();
        organizer.setAccount(organizerAccount);
        event.setOrganizer(organizer);

        Company company = new Company();
        company.setId(1);

        Location location = new Location();
        location.setId(1);

        company.setLocation(location);

        Account providerAccount = new Account();
        providerAccount.setId(VALID_PROVIDER_ID);
        Provider provider = new Provider();
        provider.setAccount(providerAccount);
        provider.setCompany(company);

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

    private void mockEventAndServiceFound() {
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(serviceRepository.findById(VALID_SERVICE_ID)).thenReturn(Optional.of(service));
    }

    @Test
    void create_WithValidInput_SavesReservation() {
        setupAvailableService(true);

        event.setDate(LocalDate.now().plusDays(10));
        createReservationDTO.setStartTime(LocalTime.of(10, 0));
        createReservationDTO.setEndTime(LocalTime.of(11, 0));

        mockEventAndServiceFound();
        when(reservationRepository.findAll()).thenReturn(List.of());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(modelMapper.map(reservation, CreatedReservationDTO.class)).thenReturn(createdReservationDTO);

        CreatedReservationDTO result = reservationService.create(createReservationDTO);

        assertThat(result).isNotNull();
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

        mockEventAndServiceFound();
        when(reservationRepository.findAll()).thenReturn(List.of());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(modelMapper.map(reservation, CreatedReservationDTO.class)).thenReturn(createdReservationDTO);

        CreatedReservationDTO result = reservationService.create(createReservationDTO);

        assertThat(result).isNotNull();
        verify(reservationRepository).save(any(Reservation.class));
        verify(modelMapper).map(reservation, CreatedReservationDTO.class);
    }

    @Test
    void create_WhenAutoConfirmIsTrue_SetsStatusToAcceptedAndBuysBudget() {
        setupAvailableService(true);

        mockEventAndServiceFound();
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

        mockEventAndServiceFound();
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
                .hasMessage("Service with ID "+ INVALID_SERVICE_ID+ "not found");

        verify(eventRepository).findById(VALID_EVENT_ID);
        verify(serviceRepository).findById(INVALID_SERVICE_ID);
        verifyNoInteractions(reservationRepository, modelMapper);
    }

    @Test
    void create_WhenServiceNotAvailable_ThrowsServiceUnavailableException() {
        service.setCurrentDetails(new ServiceDetails());
        service.getCurrentDetails().setAvailable(false);
        mockEventAndServiceFound();

        assertThatThrownBy(() -> reservationService.create(createReservationDTO))
                .isInstanceOf(ServiceUnavailableException.class)
                .hasMessage("Service is not available at selected time.");
    }

    @Test
    void create_WhenServiceAlreadyReservedForEvent_ThrowsIllegalArgumentException() {
        Reservation existingReservation = new Reservation();
        existingReservation.setEvent(event);
        existingReservation.setService(service);

        mockEventAndServiceFound();
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
        mockEventAndServiceFound();

        assertThatThrownBy(() -> reservationService.create(createReservationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reservation must be made within the reservation period.");
    }

    @Test
    void create_WhenEventHasPassed_ThrowsIllegalArgumentException() {
        event.setDate(LocalDate.now().minusDays(2));
        setupAvailableService(true);
        mockEventAndServiceFound();

        assertThatThrownBy(() -> reservationService.create(createReservationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reservation must be made within the reservation period.");
    }

    @ParameterizedTest
    @MethodSource("invalidTimes")
    void create_WithInvalidTime_ThrowsException(LocalTime start, LocalTime end, String expectedMessage) {
        createReservationDTO.setStartTime(start);
        createReservationDTO.setEndTime(end);

        mockEventAndServiceFound();

        assertThatThrownBy(() -> reservationService.create(createReservationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    private static Stream<Arguments> invalidTimes() {
        return Stream.of(
                Arguments.of(LocalTime.of(12, 0), LocalTime.of(10, 0), "End time must be after start time"),
                Arguments.of(LocalTime.of(12, 0), LocalTime.of(12, 0), "End time must be after start time"),
                Arguments.of(null, LocalTime.of(10, 0), "Start time and end time must be provided"),
                Arguments.of(LocalTime.of(10, 0), null, "Start time and end time must be provided")
        );
    }


    @Test
    void create_WhenDurationTooShort_ThrowsIllegalArgumentException() {
        service.getCurrentDetails().setMinDuration(2);
        service.getCurrentDetails().setMaxDuration(4);
        createReservationDTO.setStartTime(LocalTime.of(10, 0));
        createReservationDTO.setEndTime(LocalTime.of(11, 0));
        mockEventAndServiceFound();

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
        mockEventAndServiceFound();

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
        mockEventAndServiceFound();

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

        mockEventAndServiceFound();
        when(reservationRepository.findAll()).thenReturn(List.of(existingReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(modelMapper.map(reservation, CreatedReservationDTO.class)).thenReturn(createdReservationDTO);

        CreatedReservationDTO result = reservationService.create(createReservationDTO);

        assertThat(result).isNotNull();
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

        mockEventAndServiceFound();
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

        mockEventAndServiceFound();
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

        mockEventAndServiceFound();
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
        mockEventAndServiceFound();
        when(reservationRepository.findAll()).thenReturn(List.of(existingReservation));

        assertThatThrownBy(() -> reservationService.create(createReservationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Service not available at selected time.");

        verify(reservationRepository, never()).save(any());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void findPendingReservations_WithMatchingProviderAndPendingStatus_ReturnsMappedList() {
        Reservation pendingReservation = new Reservation();
        pendingReservation.setStatus(Status.PENDING);
        pendingReservation.setEvent(event);
        pendingReservation.setService(service);

        when(reservationRepository.findAll()).thenReturn(List.of(pendingReservation));
        when(modelMapper.map(any(Reservation.class), eq(GetReservationDTO.class)))
                .thenReturn(new GetReservationDTO());

        List<GetReservationDTO> result = reservationService.findPendingReservations(VALID_PROVIDER_ID);

        assertThat(result).hasSize(1);
        verify(reservationRepository).findAll();
    }

    @Test
    void findPendingReservations_WithNoReservations_ReturnsEmptyList() {
        when(reservationRepository.findAll()).thenReturn(Collections.emptyList());

        List<GetReservationDTO> result = reservationService.findPendingReservations(VALID_PROVIDER_ID);

        assertThat(result).isEmpty();
        verify(reservationRepository).findAll();
    }

    @Test
    void findPendingReservations_WithNoPendingStatus_ReturnsEmptyList() {
        Account providerAccount = new Account();
        providerAccount.setId(VALID_PROVIDER_ID);

        Provider provider = new Provider();
        provider.setAccount(providerAccount);

        service.setProvider(provider);

        Reservation reservation = new Reservation();
        reservation.setStatus(Status.ACCEPTED);
        reservation.setService(service);

        when(reservationRepository.findAll()).thenReturn(List.of(reservation));

        List<GetReservationDTO> result = reservationService.findPendingReservations(VALID_PROVIDER_ID);

        assertThat(result).isEmpty();
        verify(reservationRepository).findAll();
    }

    @Test
    void cancelReservation_WithValidId_ChangesStatusToCanceledAndSaves() {
        reservation.setStatus(Status.ACCEPTED);
        when(reservationRepository.findById(1)).thenReturn(Optional.of(reservation));
        when(reservationService.findServiceDetailsByReservationId(VALID_RESERVATION_ID)).thenReturn(new ServiceDetails());

        reservationService.cancelReservation(VALID_RESERVATION_ID);

        assertThat(reservation.getStatus()).isEqualTo(Status.CANCELED);
        verify(reservationRepository).save(reservation);
    }

    @Test
    void cancelReservation_WithInvalidId_ThrowsNotFoundException() {
        when(reservationRepository.findById(INVALID_RESERVATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.cancelReservation(INVALID_RESERVATION_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Reservation with ID " + INVALID_RESERVATION_ID + " not found");

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void cancelReservation_ThrowsIllegalArgumentException_WhenStatusCanceledOrDenied() {
        reservation.setStatus(Status.CANCELED);
        when(reservationRepository.findById(VALID_RESERVATION_ID)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.cancelReservation(VALID_RESERVATION_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reservation with ID " + VALID_RESERVATION_ID + " is already cancelled or unavailable");
    }

    @Test
    void cancelReservation_ThrowsIllegalArgumentException_WhenStatusPending() {
        reservation.setStatus(Status.PENDING);
        when(reservationRepository.findById(VALID_RESERVATION_ID)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.cancelReservation(VALID_RESERVATION_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot cancel a pending reservation with id  " + VALID_RESERVATION_ID);
    }

    @Test
    void acceptReservation_WithValidId_ChangesStatusToAcceptedAndSaves() {
        reservation.setStatus(Status.PENDING);
        when(reservationRepository.findById(VALID_RESERVATION_ID)).thenReturn(Optional.of(reservation));

        reservationService.acceptReservation(VALID_RESERVATION_ID);

        assertThat(reservation.getStatus()).isEqualTo(Status.ACCEPTED);
        verify(reservationRepository).save(reservation);
        verify(budgetItemService).buy(reservation.getEvent().getId(), reservation.getService().getId());
        verify(notificationService).sendNotification(
                eq(reservation.getEvent().getOrganizer().getAccount().getId()),
                eq("Reservation Accepted"),
                contains(reservation.getEvent().getName())
        );
    }

    @Test
    void acceptReservation_ThrowsNotFoundException_WhenIdNotFound() {
        when(reservationRepository.findById(INVALID_RESERVATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.acceptReservation(INVALID_RESERVATION_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Reservation with ID " + INVALID_RESERVATION_ID + " not found");

        verify(reservationRepository).findById(INVALID_RESERVATION_ID);
        verify(reservationRepository, never()).save(any());
        verify(budgetItemService, never()).buy(anyInt(), anyInt());
        verify(notificationService, never()).sendNotification(anyInt(), anyString(), anyString());
    }

    @Test
    void acceptReservation_ThrowsIllegalArgumentException_WhenStatusCanceledOrDenied() {
        reservation.setStatus(Status.DENIED);
        when(reservationRepository.findById(VALID_RESERVATION_ID)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.acceptReservation(VALID_RESERVATION_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reservation with ID " + VALID_RESERVATION_ID + " is already cancelled or unavailable");
    }

    @Test
    void acceptReservation_ThrowsIllegalArgumentException_WhenStatusAccepted() {
        reservation.setStatus(Status.ACCEPTED);
        when(reservationRepository.findById(VALID_RESERVATION_ID)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.acceptReservation(VALID_RESERVATION_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot accept a reservation that's already been accepted");
    }

    @Test
    void rejectReservation_WithValidId_ChangesStatusToDeniedAndSaves() {
        reservation.setStatus(Status.PENDING);
        when(reservationRepository.findById(VALID_RESERVATION_ID)).thenReturn(Optional.of(reservation));

        reservationService.rejectReservation(VALID_RESERVATION_ID);

        assertThat(reservation.getStatus()).isEqualTo(Status.DENIED);
        verify(reservationRepository).save(reservation);
        verifyNoInteractions(budgetItemService);
        verify(notificationService).sendNotification(
                eq(reservation.getEvent().getOrganizer().getAccount().getId()),
                eq("Reservation Denied"),
                contains(reservation.getEvent().getName())
        );
    }

    @Test
    void rejectReservation_ThrowsNotFoundException_WhenIdNotFound() {
        when(reservationRepository.findById(INVALID_RESERVATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.rejectReservation(INVALID_RESERVATION_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Reservation with ID " + INVALID_RESERVATION_ID + " not found");

        verify(reservationRepository).findById(INVALID_RESERVATION_ID);
        verify(reservationRepository, never()).save(any());
        verify(notificationService, never()).sendNotification(anyInt(), anyString(), anyString());
    }

    @Test
    void rejectReservation_ThrowsIllegalArgumentException_WhenStatusCanceledOrDenied() {
        reservation.setStatus(Status.DENIED);
        when(reservationRepository.findById(VALID_RESERVATION_ID)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.rejectReservation(VALID_RESERVATION_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reservation with ID " + VALID_RESERVATION_ID + " is already cancelled or unavailable");
    }

    @Test
    void rejectReservation_ThrowsIllegalArgumentException_WhenStatusAccepted() {
        reservation.setStatus(Status.ACCEPTED);
        when(reservationRepository.findById(VALID_RESERVATION_ID)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.rejectReservation(VALID_RESERVATION_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot reject a reservation that's already been accepted");
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