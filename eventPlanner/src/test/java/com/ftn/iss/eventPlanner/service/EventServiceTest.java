package com.ftn.iss.eventPlanner.service;

import com.ftn.iss.eventPlanner.dto.agendaitem.*;
import com.ftn.iss.eventPlanner.dto.event.CreateEventDTO;
import com.ftn.iss.eventPlanner.dto.event.CreatedEventDTO;
import com.ftn.iss.eventPlanner.dto.event.UpdateEventDTO;
import com.ftn.iss.eventPlanner.dto.event.UpdatedEventDTO;
import com.ftn.iss.eventPlanner.dto.location.CreateLocationDTO;
import com.ftn.iss.eventPlanner.dto.location.CreatedLocationDTO;
import com.ftn.iss.eventPlanner.model.*;
import com.ftn.iss.eventPlanner.repositories.*;
import com.ftn.iss.eventPlanner.services.EventService;
import com.ftn.iss.eventPlanner.services.LocationService;
import com.ftn.iss.eventPlanner.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.webjars.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventStatsRepository eventStatsRepository;
    @Mock
    private EventTypeRepository eventTypeRepository;
    @Mock
    private OrganizerRepository organizerRepository;
    @Mock
    private LocationService locationService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private EventInviteTokenRepository eventInviteTokenRepository;
    @Mock
    private AgendaItemRepository agendaItemRepository;

    @Captor
    private ArgumentCaptor<Event> eventCaptor;

    @Captor
    private ArgumentCaptor<EventStats> eventStatsCaptor;
    @Captor
    private ArgumentCaptor<AgendaItem> agendaItemCaptor;

    @InjectMocks
    private EventService eventService;

    private CreateLocationDTO createLocationDTO;
    private CreatedLocationDTO createdLocationDTO;
    private Location location;
    private CreateLocationDTO updateLocationDTO;
    private CreatedLocationDTO updatedLocationDTO;
    private Location updatedLocation;
    private EventType eventType;
    private EventType updatedEventType;
    private Organizer organizer;
    private EventStats emptyStats;
    private EventStats fullStats;
    private EventStats updatedStats;
    private List<String> guestList;
    private Reservation reservation;
    private List<Reservation> reservations;
    private CreateEventDTO createEventDTO;
    private CreatedEventDTO createdEventDTO;
    private Event createMappedEvent;
    private Event createdEvent;
    private UpdateEventDTO updateEventDTO;
    private Event initialUpdateEvent;
    private UpdatedEventDTO updatedEventDTO;
    private Event updatedEvent;
    private Event fullEvent;
    private CreateAgendaItemDTO createAgendaItemDTO;
    private AgendaItem createMappedAgendaItem;
    private AgendaItem createdAgendaItem;
    private UpdateAgendaItemDTO updateAgendaItemDTO;
    private AgendaItem updatedAgendaItem;



    @BeforeEach
    void setUp() {
        createLocationDTO = new CreateLocationDTO();
        createLocationDTO.setStreet("123 Street");
        createLocationDTO.setHouseNumber("456");
        createLocationDTO.setCity("Testville");
        createLocationDTO.setCountry("Testland");

        createdLocationDTO = new CreatedLocationDTO();
        createdLocationDTO.setStreet("123 Street");
        createdLocationDTO.setHouseNumber("456");
        createdLocationDTO.setCity("Testville");
        createdLocationDTO.setCountry("Testland");
        createdLocationDTO.setId(1);

        updateLocationDTO = new CreateLocationDTO();
        updateLocationDTO.setCity("Novi Sad");
        updateLocationDTO.setCountry("Serbia");
        updateLocationDTO.setStreet("Bulevar Oslobodjenja");
        updateLocationDTO.setHouseNumber("125");

        updatedLocationDTO = new CreatedLocationDTO();
        updatedLocationDTO.setId(2);
        updatedLocationDTO.setCity("Novi Sad");
        updatedLocationDTO.setCountry("Serbia");
        updatedLocationDTO.setStreet("Bulevar Oslobodjenja");
        updatedLocationDTO.setHouseNumber("125");

        location = new Location();
        location.setId(1);
        location.setStreet("123 Street");
        location.setHouseNumber("456");
        location.setCity("Testville");
        location.setCountry("Testland");

        updatedLocation = new Location();
        updatedLocation.setId(2);
        updatedLocation.setCity("Novi Sad");
        updatedLocation.setCountry("Serbia");
        updatedLocation.setStreet("Bulevar Oslobodjenja");
        updatedLocation.setHouseNumber("125");

        eventType = new EventType();
        eventType.setId(1);
        eventType.setName("Conference");
        eventType.setDescription("A formal meeting for discussion.");
        eventType.setActive(true);

        updatedEventType = new EventType();
        updatedEventType.setId(2);
        updatedEventType.setName("Workshop");
        updatedEventType.setDescription("An interactive training session.");
        updatedEventType.setActive(true);

        organizer = new Organizer();
        organizer.setFirstName("John");
        organizer.setLastName("Doe");
        organizer.setPhoneNumber("123456789");
        organizer.setLocation(location);
        organizer.setId(1);

        emptyStats = new EventStats();
        emptyStats.setId(1);

        fullStats = new EventStats();
        fullStats.setId(1);
        fullStats.setParticipantsCount(2);
        fullStats.setFiveStarCount(5);
        fullStats.setFourStarCount(4);
        fullStats.setThreeStarCount(3);
        fullStats.setTwoStarCount(2);
        fullStats.setOneStarCount(1);

        updatedStats = new EventStats();
        updatedStats.setId(1);
        updatedStats.setParticipantsCount(0);
        updatedStats.setFiveStarCount(5);
        updatedStats.setFourStarCount(4);
        updatedStats.setThreeStarCount(3);
        updatedStats.setTwoStarCount(2);
        updatedStats.setOneStarCount(1);

        guestList =new ArrayList<>();
        guestList.add("example@mail.com");
        guestList.add("example2@mail.com");

        reservation = new Reservation();
        reservation.setId(1);
        reservation.setStatus(Status.ACCEPTED);
        reservation.setStartTime(LocalTime.of(10,0));
        reservation.setEndTime(LocalTime.of(12,0));
        reservations = new ArrayList<>();
        reservations.add(reservation);

        createEventDTO = new CreateEventDTO();
        createEventDTO.setEventTypeId(1);
        createEventDTO.setOrganizerId(1);
        createEventDTO.setName("Test Event");
        createEventDTO.setDescription("Test description");
        createEventDTO.setMaxParticipants(100);
        createEventDTO.setOpen(true);
        createEventDTO.setDate(LocalDate.now().plusDays(5));
        createEventDTO.setLocation(createLocationDTO);

        createdEventDTO = new CreatedEventDTO();
        createdEventDTO.setId(1);
        createdEventDTO.setEventTypeId(1);
        createdEventDTO.setOrganizerId(1);
        createdEventDTO.setName("Test Event");
        createdEventDTO.setDescription("Test description");
        createdEventDTO.setMaxParticipants(100);
        createdEventDTO.setOpen(true);
        createdEventDTO.setDate(createEventDTO.getDate());
        createdEventDTO.setDeleted(false);
        createdEventDTO.setLocation(createdLocationDTO);

        createdEvent = new Event();
        createdEvent.setName(createEventDTO.getName());
        createdEvent.setDescription(createEventDTO.getDescription());
        createdEvent.setDate(createEventDTO.getDate());
        createdEvent.setMaxParticipants(createEventDTO.getMaxParticipants());
        createdEvent.setOpen(createEventDTO.isOpen());
        createdEvent.setDeleted(false);
        createdEvent.setDateCreated(LocalDateTime.now());
        createdEvent.setLocation(location);
        createdEvent.setEventType(eventType);
        createdEvent.setStats(emptyStats);
        createdEvent.setOrganizer(organizer);

        createMappedEvent = new Event();
        createMappedEvent.setName(createEventDTO.getName());
        createMappedEvent.setDescription(createEventDTO.getDescription());
        createMappedEvent.setDate(createEventDTO.getDate());
        createMappedEvent.setMaxParticipants(createEventDTO.getMaxParticipants());
        createMappedEvent.setOpen(createEventDTO.isOpen());

        initialUpdateEvent = new Event();
        initialUpdateEvent.setId(1);
        initialUpdateEvent.setName("Test Event");
        initialUpdateEvent.setDescription("Test description");
        initialUpdateEvent.setMaxParticipants(100);
        initialUpdateEvent.setOpen(true);
        initialUpdateEvent.setDate(LocalDate.now().plusDays(5));
        initialUpdateEvent.setDeleted(false);
        initialUpdateEvent.setEventType(eventType);
        initialUpdateEvent.setOrganizer(organizer);
        initialUpdateEvent.setLocation(location);
        initialUpdateEvent.setStats(fullStats);
        initialUpdateEvent.setGuestList(new ArrayList<>());

        fullEvent = new Event();
        fullEvent.setId(1);
        fullEvent.setName("Test Event");
        fullEvent.setDescription("Test description");
        fullEvent.setMaxParticipants(100);
        fullEvent.setOpen(true);
        fullEvent.setDate(LocalDate.now().plusDays(5));
        fullEvent.setDeleted(false);
        fullEvent.setEventType(eventType);
        fullEvent.setOrganizer(organizer);
        fullEvent.setLocation(location);
        fullEvent.setStats(fullStats);
        fullEvent.setGuestList(new ArrayList<>());
        fullEvent.setAgenda(new HashSet<>());

        updateEventDTO = new UpdateEventDTO();
        updateEventDTO.setName("Updated Test Event");
        updateEventDTO.setDescription("Updated Test description");
        updateEventDTO.setMaxParticipants(150);
        updateEventDTO.setOpen(false);
        updateEventDTO.setDate(LocalDate.now().plusDays(10));
        updateEventDTO.setEventTypeId(2);
        updateEventDTO.setLocation(updateLocationDTO);

        updatedEvent = new Event();
        updatedEvent.setId(1);
        updatedEvent.setName("Updated Test Event");
        updatedEvent.setDescription("Updated Test description");
        updatedEvent.setMaxParticipants(150);
        updatedEvent.setOpen(false);
        updatedEvent.setDate(LocalDate.now().plusDays(10));
        updatedEvent.setDeleted(false);
        updatedEvent.setEventType(updatedEventType);
        updatedEvent.setOrganizer(organizer);
        updatedEvent.setLocation(updatedLocation);
        updatedEvent.setGuestList(new ArrayList<>());

        updatedEventDTO = new UpdatedEventDTO();
        updatedEventDTO.setId(1);
        updatedEventDTO.setName("Updated Test Event");
        updatedEventDTO.setDescription("Updated Test description");
        updatedEventDTO.setMaxParticipants(150);
        updatedEventDTO.setOpen(false);
        updatedEventDTO.setDate(LocalDate.now().plusDays(10));
        updatedEventDTO.setEventTypeId(2);
        updatedEventDTO.setLocation(updatedLocationDTO);
        updatedEventDTO.setDeleted(false);

        createAgendaItemDTO = new CreateAgendaItemDTO();
        createAgendaItemDTO.setName("Opening Ceremony");
        createAgendaItemDTO.setDescription("Welcome speech and overview of the event.");
        createAgendaItemDTO.setLocation("Main Hall");
        createAgendaItemDTO.setStartTime(LocalTime.of(9, 0));
        createAgendaItemDTO.setEndTime(LocalTime.of(10, 0));

        createMappedAgendaItem = new AgendaItem();
        createMappedAgendaItem.setName("Opening Ceremony");
        createMappedAgendaItem.setDescription("Welcome speech and overview of the event.");
        createMappedAgendaItem.setLocation("Main Hall");
        createMappedAgendaItem.setStartTime(LocalTime.of(9, 0));
        createMappedAgendaItem.setEndTime(LocalTime.of(10, 0));

        createdAgendaItem = new AgendaItem();
        createdAgendaItem.setName("Opening Ceremony");
        createdAgendaItem.setDescription("Welcome speech and overview of the event.");
        createdAgendaItem.setLocation("Main Hall");
        createdAgendaItem.setStartTime(LocalTime.of(9, 0));
        createdAgendaItem.setEndTime(LocalTime.of(10, 0));
        createdAgendaItem.setId(1);
        createdAgendaItem.setDeleted(false);
        createdAgendaItem.setEvent(fullEvent);

        updateAgendaItemDTO = new UpdateAgendaItemDTO();
        updateAgendaItemDTO.setName("Keynote Speech");
        updateAgendaItemDTO.setDescription("An inspiring opening speech by the keynote speaker.");
        updateAgendaItemDTO.setLocation("Auditorium A");
        updateAgendaItemDTO.setStartTime(LocalTime.of(10, 0));
        updateAgendaItemDTO.setEndTime(LocalTime.of(11, 0));

        updatedAgendaItem = new AgendaItem();
        updatedAgendaItem.setId(1);
        updatedAgendaItem.setName(updateAgendaItemDTO.getName());
        updatedAgendaItem.setDescription(updateAgendaItemDTO.getDescription());
        updatedAgendaItem.setLocation(updateAgendaItemDTO.getLocation());
        updatedAgendaItem.setStartTime(updateAgendaItemDTO.getStartTime());
        updatedAgendaItem.setEndTime(updateAgendaItemDTO.getEndTime());
        updatedAgendaItem.setDeleted(false);
        updatedAgendaItem.setEvent(fullEvent);
    }

    @Test
    @DisplayName("Should create event successfully when data is valid")
    void create_WithValidData_CreatesEvent() {
        when(modelMapper.map(createEventDTO, Event.class)).thenReturn(createMappedEvent);
        when(eventTypeRepository.findById(1)).thenReturn(Optional.of(eventType));
        when(organizerRepository.findById(1)).thenReturn(Optional.of(organizer));
        when(eventStatsRepository.save(any(EventStats.class))).thenReturn(emptyStats);
        when(eventRepository.save(any(Event.class))).thenReturn(createdEvent);
        when(locationService.create(createLocationDTO)).thenReturn(createdLocationDTO);
        when(modelMapper.map(createdLocationDTO, Location.class)).thenReturn(location);
        when(modelMapper.map(any(Event.class), eq(CreatedEventDTO.class))).thenReturn(createdEventDTO);

        CreatedEventDTO result = eventService.create(createEventDTO);

        verify(eventRepository,times(1)).save(eventCaptor.capture());
        Event savedEvent = eventCaptor.getValue();

        assertTrue(compareEvents(savedEvent, createdEvent));
    }

    @Test
    @DisplayName("Should throw exception when event date is in the past")
    void create_WhenEventDateIsInThePast_ThrowsException() {
        createEventDTO.setDate(LocalDate.now().minusDays(1));
        createMappedEvent.setDate(createEventDTO.getDate());

        when(modelMapper.map(createEventDTO, Event.class)).thenReturn(createMappedEvent);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.create(createEventDTO);
        });

        assertEquals("Event date must be in the future", exception.getMessage());
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create event successfully when date is tomorrow")
    void create_WhenEventDateIsTomorrow_CreatesEvent() {
        createEventDTO.setDate(LocalDate.now().plusDays(1));
        createdEventDTO.setDate(LocalDate.now().plusDays(1));
        createMappedEvent.setDate(LocalDate.now().plusDays(1));
        createdEvent.setDate(LocalDate.now().plusDays(1));

        when(modelMapper.map(createEventDTO, Event.class)).thenReturn(createMappedEvent);
        when(eventTypeRepository.findById(1)).thenReturn(Optional.of(eventType));
        when(organizerRepository.findById(1)).thenReturn(Optional.of(organizer));
        when(eventStatsRepository.save(any(EventStats.class))).thenReturn(emptyStats);
        when(eventRepository.save(any(Event.class))).thenReturn(createdEvent);
        when(locationService.create(createLocationDTO)).thenReturn(createdLocationDTO);
        when(modelMapper.map(createdLocationDTO, Location.class)).thenReturn(location);
        when(modelMapper.map(any(Event.class), eq(CreatedEventDTO.class))).thenReturn(createdEventDTO);

        CreatedEventDTO result = eventService.create(createEventDTO);

        verify(eventRepository,times(1)).save(eventCaptor.capture());
        Event savedEvent = eventCaptor.getValue();

        assertTrue(compareEvents(savedEvent, createdEvent));
    }

    @Test
    @DisplayName("Should throw exception when EventType is not found")
    void create_WhenEventTypeNotFound_ThrowsException() {
        when(modelMapper.map(createEventDTO, Event.class)).thenReturn(createMappedEvent);
        when(eventTypeRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.create(createEventDTO);
        });

        assertEquals("Event Type with ID 1 not found", exception.getMessage());
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when Organizer is not found")
    void create_WhenOrganizerNotFound_ThrowsException() {
        when(modelMapper.map(createEventDTO, Event.class)).thenReturn(createMappedEvent);
        when(eventTypeRepository.findById(1)).thenReturn(Optional.of(eventType));
        when(organizerRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.create(createEventDTO);
        });

        assertEquals("Organizer with ID 1 not found", exception.getMessage());
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create event successfully without event type")
    void create_WithoutEventType_CreatesEvent() {
        createEventDTO.setEventTypeId(0);
        createdEvent.setEventType(null);
        createdEventDTO.setEventTypeId(0);

        when(modelMapper.map(createEventDTO, Event.class)).thenReturn(createMappedEvent);
        when(organizerRepository.findById(1)).thenReturn(Optional.of(organizer));
        when(eventStatsRepository.save(any(EventStats.class))).thenReturn(emptyStats);
        when(eventRepository.save(any(Event.class))).thenReturn(createdEvent);
        when(locationService.create(createLocationDTO)).thenReturn(createdLocationDTO);
        when(modelMapper.map(createdLocationDTO, Location.class)).thenReturn(location);
        when(modelMapper.map(any(Event.class), eq(CreatedEventDTO.class))).thenReturn(createdEventDTO);

        eventService.create(createEventDTO);

        verify(eventRepository,times(1)).save(eventCaptor.capture());
        Event savedEvent = eventCaptor.getValue();

        assertNull(savedEvent.getEventType());
        assertTrue(compareEvents(savedEvent, createdEvent));
    }

    @Test
    @DisplayName("Should update event successfully when data is valid")
    void update_WithValidData_UpdatesEvent() {
        when(eventRepository.findById(1)).thenReturn(Optional.of(initialUpdateEvent));
        when(reservationRepository.findByEventId(1)).thenReturn(new ArrayList<>());
        when(eventTypeRepository.findById(2)).thenReturn(Optional.of(updatedEventType));
        when(locationService.create(updateLocationDTO)).thenReturn(updatedLocationDTO);
        when(modelMapper.map(updatedLocationDTO, Location.class)).thenReturn(updatedLocation);
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);
        when(modelMapper.map(updatedEvent, UpdatedEventDTO.class)).thenReturn(updatedEventDTO);
        when(accountRepository.findAccountsByAcceptedEventId(1)).thenReturn(new ArrayList<>());



        UpdatedEventDTO result = eventService.update(1,updateEventDTO);

        verify(eventRepository,times(1)).save(eventCaptor.capture());
        Event savedEvent = eventCaptor.getValue();

        verify(eventStatsRepository, times(1)).save(eventStatsCaptor.capture());
        EventStats savedStats = eventStatsCaptor.getValue();

        assertTrue(compareEventStats(savedStats, updatedStats));
        assertTrue(compareEvents(savedEvent, updatedEvent));
    }

    @Test
    @DisplayName("Should throw exception when event doesn't exist")
    void update_WhenEventNotFound_ThrowsException() {
        when(eventRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.update(1,updateEventDTO);
        });

        assertEquals("Event with ID 1 not found", exception.getMessage());
        verify(eventRepository, never()).save(any());
    }


    @Test
    @DisplayName("Should throw exception when event date is in the past during update")
    void update_WhenEventDateIsInThePast_ThrowsException() {
        updateEventDTO.setDate(LocalDate.now().minusDays(1));

        when(eventRepository.findById(1)).thenReturn(Optional.of(initialUpdateEvent));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.update(1, updateEventDTO);
        });

        assertEquals("Event date must be in the future", exception.getMessage());
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when event has reservations and date is changed")
    void update_WhenEventHasReservationsAndDateChanged_ThrowsException() {
        when(eventRepository.findById(1)).thenReturn(Optional.of(initialUpdateEvent));
        when(reservationRepository.findByEventId(1)).thenReturn(reservations);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.update(1, updateEventDTO);
        });

        assertEquals("Event date can't be changed when it has reservations", exception.getMessage());
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when event has pending reservations and date is changed")
    void update_WhenEventHasPendingReservationsAndDateChanged_ThrowsException() {
        reservation.setStatus(Status.PENDING);
        reservations=new ArrayList<>();
        reservations.add(reservation);

        when(eventRepository.findById(1)).thenReturn(Optional.of(initialUpdateEvent));
        when(reservationRepository.findByEventId(1)).thenReturn(reservations);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.update(1, updateEventDTO);
        });

        assertEquals("Event date can't be changed when it has reservations", exception.getMessage());
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update event successfully with canceled reservations")
    void update_WhenEventHasCanceledReservationsAndDateChanged_UpdatesEvent() {
        reservation.setStatus(Status.CANCELED);
        reservations = new ArrayList<>();
        reservations.add(reservation);

        when(eventRepository.findById(1)).thenReturn(Optional.of(initialUpdateEvent));
        when(reservationRepository.findByEventId(1)).thenReturn(reservations);
        when(eventTypeRepository.findById(2)).thenReturn(Optional.of(updatedEventType));
        when(locationService.create(updateLocationDTO)).thenReturn(updatedLocationDTO);
        when(modelMapper.map(updatedLocationDTO, Location.class)).thenReturn(updatedLocation);
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);
        when(modelMapper.map(updatedEvent, UpdatedEventDTO.class)).thenReturn(updatedEventDTO);
        when(accountRepository.findAccountsByAcceptedEventId(1)).thenReturn(new ArrayList<>());

        UpdatedEventDTO result = eventService.update(1, updateEventDTO);

        verify(eventRepository, times(1)).save(eventCaptor.capture());
        Event savedEvent = eventCaptor.getValue();

        assertTrue(compareEvents(savedEvent, updatedEvent));
    }

    @Test
    @DisplayName("Should update event successfully when it has reservations and the date is same")
    void update_WhenEventHasReservationsAndDateIsSame_UpdatesEvent() {
        updateEventDTO.setDate(initialUpdateEvent.getDate());
        updatedEvent.setDate(initialUpdateEvent.getDate());
        updatedEventDTO.setDate(initialUpdateEvent.getDate());

        when(eventRepository.findById(1)).thenReturn(Optional.of(initialUpdateEvent));
        when(eventTypeRepository.findById(2)).thenReturn(Optional.of(updatedEventType));
        when(locationService.create(updateLocationDTO)).thenReturn(updatedLocationDTO);
        when(modelMapper.map(updatedLocationDTO, Location.class)).thenReturn(updatedLocation);
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);
        when(modelMapper.map(updatedEvent, UpdatedEventDTO.class)).thenReturn(updatedEventDTO);
        when(accountRepository.findAccountsByAcceptedEventId(1)).thenReturn(new ArrayList<>());

        UpdatedEventDTO result = eventService.update(1, updateEventDTO);

        verify(eventRepository, times(1)).save(any(Event.class));
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should throw exception when max participants is less than current participants")
    void update_WhenMaxParticipantsLessThanCurrentParticipants_ThrowsException() {
        updateEventDTO.setMaxParticipants(1);

        when(eventRepository.findById(1)).thenReturn(Optional.of(initialUpdateEvent));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.update(1, updateEventDTO);
        });

        assertEquals("Max participants cannot be less than current participants count", exception.getMessage());
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when event type is not found during update")
    void update_WhenEventTypeNotFound_ThrowsException() {
        when(eventRepository.findById(1)).thenReturn(Optional.of(initialUpdateEvent));
        when(eventTypeRepository.findById(2)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.update(1, updateEventDTO);
        });

        assertEquals("Event Type with ID 2 not found", exception.getMessage());
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update event successfully without event type")
    void update_WithoutEventType_UpdatesEvent() {
        updateEventDTO.setEventTypeId(0);
        updatedEvent.setEventType(null);
        updatedEventDTO.setEventTypeId(0);

        when(eventRepository.findById(1)).thenReturn(Optional.of(initialUpdateEvent));
        when(reservationRepository.findByEventId(1)).thenReturn(new ArrayList<>());
        when(locationService.create(updateLocationDTO)).thenReturn(updatedLocationDTO);
        when(modelMapper.map(updatedLocationDTO, Location.class)).thenReturn(updatedLocation);
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);
        when(modelMapper.map(updatedEvent, UpdatedEventDTO.class)).thenReturn(updatedEventDTO);
        when(accountRepository.findAccountsByAcceptedEventId(1)).thenReturn(new ArrayList<>());

        UpdatedEventDTO result = eventService.update(1, updateEventDTO);

        verify(eventRepository, times(1)).save(eventCaptor.capture());
        Event savedEvent = eventCaptor.getValue();

        assertNull(savedEvent.getEventType());
        verify(eventTypeRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should update event publicity from private to public")
    void update_WhenChangingFromPrivateToPublic_ClearsGuestListAndResetsStats() {
        initialUpdateEvent.setOpen(false);
        initialUpdateEvent.setGuestList(guestList);

        Account guest1 = new Account();
        guest1.setId(1);
        guest1.setAcceptedEvents(new HashSet<>());
        guest1.getAcceptedEvents().add(initialUpdateEvent);

        Account guest2 = new Account();
        guest2.setId(2);
        guest2.setAcceptedEvents(new HashSet<>());
        guest2.getAcceptedEvents().add(initialUpdateEvent);
        List<Account> guests = List.of(guest1, guest2);

        fullStats.setParticipantsCount(2);

        updateEventDTO.setOpen(true);
        updatedEvent.setOpen(true);
        updatedEvent.setGuestList(new ArrayList<>());
        updatedEventDTO.setOpen(true);

        EventInviteToken eventInviteToken = new EventInviteToken();
        ArrayList<EventInviteToken> eventInviteTokens = new ArrayList<>();
        eventInviteTokens.add(eventInviteToken);

        when(eventRepository.findById(1)).thenReturn(Optional.of(initialUpdateEvent));
        when(reservationRepository.findByEventId(1)).thenReturn(new ArrayList<>());
        when(eventTypeRepository.findById(2)).thenReturn(Optional.of(updatedEventType));
        when(locationService.create(updateLocationDTO)).thenReturn(updatedLocationDTO);
        when(modelMapper.map(updatedLocationDTO, Location.class)).thenReturn(updatedLocation);
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);
        when(modelMapper.map(updatedEvent, UpdatedEventDTO.class)).thenReturn(updatedEventDTO);
        when(accountRepository.findAccountsByAcceptedEventId(1)).thenReturn(guests);
        when(eventInviteTokenRepository.findAllByEventId(1)).thenReturn(eventInviteTokens);

        UpdatedEventDTO result = eventService.update(1, updateEventDTO);

        verify(eventRepository, times(1)).save(eventCaptor.capture());
        Event savedEvent = eventCaptor.getValue();

        verify(eventStatsRepository, times(1)).save(eventStatsCaptor.capture());
        EventStats savedStats = eventStatsCaptor.getValue();

        verify(accountRepository, times(2)).save(any(Account.class));

        verify(eventInviteTokenRepository,times(1)).deleteAll(any(Collection.class));

        assertTrue(compareEventStats(savedStats, updatedStats));
        assertTrue(compareEvents(savedEvent, updatedEvent));
    }

    @Test
    @DisplayName("Should update event publicity from public to private")
    void update_WhenChangingFromPublicToPrivate_ResetsStats() {
        initialUpdateEvent.setOpen(true);

        updateEventDTO.setOpen(false);
        updatedEvent.setOpen(false);
        updatedEventDTO.setOpen(false);

        when(eventRepository.findById(1)).thenReturn(Optional.of(initialUpdateEvent));
        when(reservationRepository.findByEventId(1)).thenReturn(new ArrayList<>());
        when(eventTypeRepository.findById(2)).thenReturn(Optional.of(updatedEventType));
        when(locationService.create(updateLocationDTO)).thenReturn(updatedLocationDTO);
        when(modelMapper.map(updatedLocationDTO, Location.class)).thenReturn(updatedLocation);
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);
        when(modelMapper.map(updatedEvent, UpdatedEventDTO.class)).thenReturn(updatedEventDTO);
        when(accountRepository.findAccountsByAcceptedEventId(1)).thenReturn(new ArrayList<>());

        UpdatedEventDTO result = eventService.update(1, updateEventDTO);

        verify(eventRepository, times(1)).save(eventCaptor.capture());
        Event savedEvent = eventCaptor.getValue();

        verify(eventStatsRepository, times(1)).save(eventStatsCaptor.capture());
        EventStats savedStats = eventStatsCaptor.getValue();

        assertTrue(compareEvents(savedEvent,updatedEvent));
        assertEquals(0, savedStats.getParticipantsCount());
    }

    @Test
    @DisplayName("Should not change stats when open status is the same")
    void update_WhenOpenStatusSame_DoesNotChangeStats() {
        initialUpdateEvent.setOpen(true);
        updateEventDTO.setOpen(true);
        updatedEvent.setOpen(true);
        updatedEventDTO.setOpen(true);

        when(eventRepository.findById(1)).thenReturn(Optional.of(initialUpdateEvent));
        when(reservationRepository.findByEventId(1)).thenReturn(new ArrayList<>());
        when(eventTypeRepository.findById(2)).thenReturn(Optional.of(updatedEventType));
        when(locationService.create(updateLocationDTO)).thenReturn(updatedLocationDTO);
        when(modelMapper.map(updatedLocationDTO, Location.class)).thenReturn(updatedLocation);
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);
        when(modelMapper.map(updatedEvent, UpdatedEventDTO.class)).thenReturn(updatedEventDTO);
        when(accountRepository.findAccountsByAcceptedEventId(1)).thenReturn(new ArrayList<>());

        UpdatedEventDTO result = eventService.update(1, updateEventDTO);

        verify(eventRepository, times(1)).save(eventCaptor.capture());
        Event savedEvent = eventCaptor.getValue();

        verify(eventStatsRepository, times(0)).save(any(EventStats.class));

        assertTrue(compareEvents(savedEvent, updatedEvent));
    }


    @Test
    @DisplayName("Should send notifications to all accepted guests")
    void update_WithAcceptedGuests_SendsNotifications() {
        Account guest1 = new Account();
        guest1.setId(1);
        Account guest2 = new Account();
        guest2.setId(2);
        List<Account> guests = List.of(guest1, guest2);

        when(eventRepository.findById(1)).thenReturn(Optional.of(initialUpdateEvent));
        when(reservationRepository.findByEventId(1)).thenReturn(new ArrayList<>());
        when(eventTypeRepository.findById(2)).thenReturn(Optional.of(updatedEventType));
        when(locationService.create(updateLocationDTO)).thenReturn(updatedLocationDTO);
        when(modelMapper.map(updatedLocationDTO, Location.class)).thenReturn(updatedLocation);
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);
        when(modelMapper.map(updatedEvent, UpdatedEventDTO.class)).thenReturn(updatedEventDTO);
        when(accountRepository.findAccountsByAcceptedEventId(1)).thenReturn(guests).thenReturn(List.of());

        UpdatedEventDTO result = eventService.update(1, updateEventDTO);

        verify(notificationService, times(2)).sendNotification(anyInt(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should send notifications to all accepted guests")
    void delete_WithoutReservations_DeletesEvent() {
        when(eventRepository.findById(1)).thenReturn(Optional.of(fullEvent));
        when(reservationRepository.findByEventId(1)).thenReturn(new ArrayList<>());
        when(eventRepository.save(any(Event.class))).thenReturn(fullEvent);
        when(accountRepository.findAccountsByAcceptedEventId(1)).thenReturn(List.of());

        eventService.delete(1);

        verify(eventRepository, times(1)).save(eventCaptor.capture());
        Event savedEvent = eventCaptor.getValue();
        assertTrue(savedEvent.isDeleted());
    }

    @Test
    @DisplayName("Should throw NotFoundException when event is not found")
    void delete_WhenEventNotFound_ThrowsException() {
        when(eventRepository.findById(1)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            eventService.delete(1);
        });

        assertEquals("Event with ID 1 not found", exception.getMessage());
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when event has accepted reservation and is in the future")
    void delete_WhenEventHasAcceptedReservation_ThrowsException() {
        fullEvent.setDate(LocalDate.now().plusDays(1));
        reservation.setStatus(Status.ACCEPTED);
        reservations = List.of(reservation);

        when(eventRepository.findById(1)).thenReturn(Optional.of(fullEvent));
        when(reservationRepository.findByEventId(1)).thenReturn(reservations);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.delete(1);
        });

        assertEquals("Event can't be deleted when it has reservations", exception.getMessage());
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when event has pending reservation and is in the future")
    void delete_WhenEventHasPendingReservation_ThrowsException() {
        fullEvent.setDate(LocalDate.now().plusDays(1));
        reservation.setStatus(Status.PENDING);
        reservations = List.of(reservation);

        when(eventRepository.findById(1)).thenReturn(Optional.of(fullEvent));
        when(reservationRepository.findByEventId(1)).thenReturn(reservations);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.delete(1);
        });

        assertEquals("Event can't be deleted when it has reservations", exception.getMessage());
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete event without checking for invitations if it is in the past")
    void delete_WhenEventIsInPast_DeletesImmediately() {
        fullEvent.setDate(LocalDate.now().minusDays(1));

        when(eventRepository.findById(1)).thenReturn(Optional.of(fullEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(fullEvent);

        eventService.delete(1);

        verify(reservationRepository, never()).findByEventId(anyInt());
        verify(eventRepository).save(eventCaptor.capture());
        verify(eventRepository).flush();

        Event deletedEvent = eventCaptor.getValue();
        assertTrue(deletedEvent.isDeleted());
    }

    @Test
    @DisplayName("Should notify guests if event is in the future")
    void delete_WhenEventInFuture_NotifiesGuests() {
        fullEvent.setDate(LocalDate.now().plusDays(5));

        Account guest1 = new Account();
        guest1.setId(1);
        Account guest2 = new Account();
        guest2.setId(2);
        List<Account> guests = List.of(guest1, guest2);

        when(eventRepository.findById(1)).thenReturn(Optional.of(fullEvent));
        when(reservationRepository.findByEventId(1)).thenReturn(List.of());
        when(accountRepository.findAccountsByAcceptedEventId(1)).thenReturn(guests);
        when(eventRepository.save(eventCaptor.capture())).thenReturn(fullEvent);

        eventService.delete(1);

        verify(notificationService, times(2))
                .sendNotification(anyInt(), eq("Event Cancelled"), eq("The event Test Event has been cancelled."));
        verify(eventRepository).flush();

        Event deletedEvent = eventCaptor.getValue();
        assertTrue(deletedEvent.isDeleted());
    }

    @Test
    @DisplayName("Should create agenda item successfully")
    void createAgendaItem_WithValidData_CreatesAgendaItem() {
        when(eventRepository.findById(1)).thenReturn(Optional.of(fullEvent));
        when(modelMapper.map(createAgendaItemDTO, AgendaItem.class)).thenReturn(createMappedAgendaItem);
        when(agendaItemRepository.save(any(AgendaItem.class))).thenReturn(createdAgendaItem);

        eventService.createAgendaItem(1, createAgendaItemDTO);

        verify(agendaItemRepository, times(1)).save(agendaItemCaptor.capture());
        verify(eventRepository, times(1)).save(eventCaptor.capture());

        AgendaItem savedAgendaItem = agendaItemCaptor.getValue();
        assertTrue(compareAgendaItems(savedAgendaItem, createdAgendaItem));

        Event savedEvent = eventCaptor.getValue();
        assertTrue(savedEvent.getAgenda().contains(savedAgendaItem));
    }

    @Test
    @DisplayName("Should throw exception when start time is after end time")
    void createAgendaItem_WhenStartTimeAfterEndTime_ThrowsException() {
        createAgendaItemDTO.setStartTime(LocalTime.of(12, 0));
        createAgendaItemDTO.setEndTime(LocalTime.of(10, 0));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createAgendaItem(1, createAgendaItemDTO);
        });

        assertEquals("Start time must be before end time", exception.getMessage());
        verify(eventRepository, never()).save(any());
        verify(agendaItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw NotFoundException when event does not exist")
    void createAgendaItem_WhenEventNotFound_ThrowsNotFoundException() {
        when(eventRepository.findById(1)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            eventService.createAgendaItem(1, createAgendaItemDTO);
        });

        assertEquals("Event with ID 1 not found", exception.getMessage());
        verify(agendaItemRepository, never()).save(any());
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update agenda item successfully")
    void updateAgendaItem_WithValidData_UpdatesAgendaItem() {
        fullEvent.getAgenda().add(createdAgendaItem);

        when(eventRepository.findById(1)).thenReturn(Optional.of(fullEvent));
        when(agendaItemRepository.findById(1)).thenReturn(Optional.of(createdAgendaItem));
        when(agendaItemRepository.save(any(AgendaItem.class))).thenReturn(updatedAgendaItem);

        eventService.updateAgendaItem(1,1, updateAgendaItemDTO);

        verify(agendaItemRepository, times(1)).save(agendaItemCaptor.capture());

        AgendaItem savedAgendaItem = agendaItemCaptor.getValue();
        assertTrue(compareAgendaItems(savedAgendaItem, updatedAgendaItem));
    }

    @Test
    @DisplayName("Should throw exception when start time is after end time")
    void updateAgendaItem_WhenStartTimeAfterEndTime_ThrowsException() {
        updateAgendaItemDTO.setStartTime(LocalTime.of(12, 0));
        updateAgendaItemDTO.setEndTime(LocalTime.of(10, 0));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.updateAgendaItem(1, 1, updateAgendaItemDTO);
        });

        assertEquals("Start time must be before end time", exception.getMessage());
        verify(eventRepository, never()).findById(anyInt());
        verify(agendaItemRepository, never()).findById(anyInt());
        verify(agendaItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw NotFoundException when event does not exist")
    void updateAgendaItem_WhenEventNotFound_ThrowsNotFoundException() {
        when(eventRepository.findById(1)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            eventService.updateAgendaItem(1, 1, updateAgendaItemDTO);
        });

        assertEquals("Event with ID 1 not found", exception.getMessage());
        verify(agendaItemRepository, never()).findById(anyInt());
        verify(agendaItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw NotFoundException when agenda item does not exist")
    void updateAgendaItem_WhenAgendaItemNotFound_ThrowsNotFoundException() {
        when(eventRepository.findById(1)).thenReturn(Optional.of(fullEvent));
        when(agendaItemRepository.findById(1)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            eventService.updateAgendaItem(1, 1, updateAgendaItemDTO);
        });

        assertEquals("Agenda Item with ID 1 not found", exception.getMessage());
        verify(agendaItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when agenda item is not part of the event")
    void updateAgendaItem_WhenAgendaItemNotPartOfEvent_ThrowsException() {
        // Create an agenda item that's not in the event's agenda
        AgendaItem unrelatedAgendaItem = new AgendaItem();
        unrelatedAgendaItem.setId(2);
        unrelatedAgendaItem.setName("Unrelated Item");

        when(eventRepository.findById(1)).thenReturn(Optional.of(fullEvent));
        when(agendaItemRepository.findById(2)).thenReturn(Optional.of(unrelatedAgendaItem));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.updateAgendaItem(1, 2, updateAgendaItemDTO);
        });

        assertEquals("Agenda Item with ID 2 is not part of Event with ID 1", exception.getMessage());
        verify(agendaItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return UpdatedAgendaItemDTO after successful update")
    void updateAgendaItem_WithValidData_ReturnsUpdatedAgendaItemDTO() {
        fullEvent.getAgenda().add(createdAgendaItem);

        UpdatedAgendaItemDTO expectedDTO = new UpdatedAgendaItemDTO();
        expectedDTO.setId(1);
        expectedDTO.setName(updateAgendaItemDTO.getName());
        expectedDTO.setDescription(updateAgendaItemDTO.getDescription());
        expectedDTO.setLocation(updateAgendaItemDTO.getLocation());
        expectedDTO.setStartTime(updateAgendaItemDTO.getStartTime());
        expectedDTO.setEndTime(updateAgendaItemDTO.getEndTime());

        when(eventRepository.findById(1)).thenReturn(Optional.of(fullEvent));
        when(agendaItemRepository.findById(1)).thenReturn(Optional.of(createdAgendaItem));
        when(agendaItemRepository.save(any(AgendaItem.class))).thenReturn(updatedAgendaItem);
        when(modelMapper.map(any(AgendaItem.class), eq(UpdatedAgendaItemDTO.class))).thenReturn(expectedDTO);

        UpdatedAgendaItemDTO result = eventService.updateAgendaItem(1, 1, updateAgendaItemDTO);

        assertNotNull(result);
        assertEquals(expectedDTO.getId(), result.getId());
        assertEquals(expectedDTO.getName(), result.getName());
        assertEquals(expectedDTO.getDescription(), result.getDescription());
        assertEquals(expectedDTO.getLocation(), result.getLocation());
        assertEquals(expectedDTO.getStartTime(), result.getStartTime());
        assertEquals(expectedDTO.getEndTime(), result.getEndTime());
    }

    @Test
    @DisplayName("Should delete agenda item successfully when data is valid")
    void deleteAgendaItem_WithValidData_DeletesAgendaItem() {
        fullEvent.getAgenda().add(createdAgendaItem);

        when(eventRepository.findById(1)).thenReturn(Optional.of(fullEvent));
        when(agendaItemRepository.findById(1)).thenReturn(Optional.of(createdAgendaItem));
        when(agendaItemRepository.save(any(AgendaItem.class))).thenReturn(createdAgendaItem);

        eventService.deleteAgendaItem(1, 1);

        verify(agendaItemRepository, times(1)).save(agendaItemCaptor.capture());
        AgendaItem savedAgendaItem = agendaItemCaptor.getValue();

        assertTrue(savedAgendaItem.isDeleted());
        assertTrue(compareAgendaItems(createdAgendaItem, savedAgendaItem));
    }

    @Test
    @DisplayName("Should throw NotFoundException when event does not exist")
    void deleteAgendaItem_WhenEventNotFound_ThrowsNotFoundException() {
        when(eventRepository.findById(1)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            eventService.deleteAgendaItem(1, 1);
        });

        assertEquals("Event with ID 1 not found", exception.getMessage());
        verify(agendaItemRepository, never()).findById(anyInt());
        verify(agendaItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw NotFoundException when agenda item does not exist")
    void deleteAgendaItem_WhenAgendaItemNotFound_ThrowsNotFoundException() {
        when(eventRepository.findById(1)).thenReturn(Optional.of(fullEvent));
        when(agendaItemRepository.findById(1)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            eventService.deleteAgendaItem(1, 1);
        });

        assertEquals("Agenda Item with ID 1 not found", exception.getMessage());
        verify(agendaItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when agenda item is not part of the event")
    void deleteAgendaItem_WhenAgendaItemNotPartOfEvent_ThrowsException() {
        // Create an agenda item that's not in the event's agenda
        AgendaItem unrelatedAgendaItem = new AgendaItem();
        unrelatedAgendaItem.setId(2);
        unrelatedAgendaItem.setName("Unrelated Item");

        when(eventRepository.findById(1)).thenReturn(Optional.of(fullEvent));
        when(agendaItemRepository.findById(2)).thenReturn(Optional.of(unrelatedAgendaItem));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.deleteAgendaItem(1, 2);
        });

        assertEquals("Agenda Item with ID 2 is not part of Event with ID 1", exception.getMessage());
        verify(agendaItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return empty collection when event has no agenda items")
    void getAgenda_WhenEventHasNoAgendaItems_ReturnsEmptyCollection() {
        Event eventWithEmptyAgenda = new Event();
        eventWithEmptyAgenda.setId(1);
        eventWithEmptyAgenda.setAgenda(new HashSet<>());

        when(eventRepository.findById(1)).thenReturn(Optional.of(eventWithEmptyAgenda));

        Collection<GetAgendaItemDTO> result = eventService.getAgenda(1);

        assertTrue(result.isEmpty());
        verify(modelMapper, never()).map(any(), eq(GetAgendaItemDTO.class));
    }

    @Test
    @DisplayName("Should throw NotFoundException when event does not exist")
    void getAgenda_WhenEventNotFound_ThrowsNotFoundException() {
        when(eventRepository.findById(1)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            eventService.getAgenda(1);
        });

        assertEquals("Event with ID 1 not found", exception.getMessage());
        verify(modelMapper, never()).map(any(), eq(GetAgendaItemDTO.class));
    }

    @Test
    @DisplayName("Should return agenda items sorted by start time")
    void getAgenda_WithMultipleAgendaItems_ReturnsSortedByStartTime() {
        // Create agenda items with different start times
        AgendaItem item1 = new AgendaItem();
        item1.setId(1);
        item1.setName("Late Item");
        item1.setStartTime(LocalTime.of(14, 0));
        item1.setDeleted(false);

        AgendaItem item2 = new AgendaItem();
        item2.setId(2);
        item2.setName("Early Item");
        item2.setStartTime(LocalTime.of(9, 0));
        item2.setDeleted(false);

        AgendaItem item3 = new AgendaItem();
        item3.setId(3);
        item3.setName("Middle Item");
        item3.setStartTime(LocalTime.of(12, 0));
        item3.setDeleted(false);

        fullEvent.getAgenda().clear();
        fullEvent.getAgenda().add(item1);
        fullEvent.getAgenda().add(item2);
        fullEvent.getAgenda().add(item3);

        GetAgendaItemDTO dto1 = new GetAgendaItemDTO();
        dto1.setId(2);
        dto1.setName("Early Item");
        dto1.setStartTime(LocalTime.of(9, 0));

        GetAgendaItemDTO dto2 = new GetAgendaItemDTO();
        dto2.setId(3);
        dto2.setName("Middle Item");
        dto2.setStartTime(LocalTime.of(12, 0));

        GetAgendaItemDTO dto3 = new GetAgendaItemDTO();
        dto3.setId(1);
        dto3.setName("Late Item");
        dto3.setStartTime(LocalTime.of(14, 0));

        when(eventRepository.findById(1)).thenReturn(Optional.of(fullEvent));
        when(modelMapper.map(item2, GetAgendaItemDTO.class)).thenReturn(dto1);
        when(modelMapper.map(item3, GetAgendaItemDTO.class)).thenReturn(dto2);
        when(modelMapper.map(item1, GetAgendaItemDTO.class)).thenReturn(dto3);

        Collection<GetAgendaItemDTO> result = eventService.getAgenda(1);

        List<GetAgendaItemDTO> resultList = new ArrayList<>(result);
        assertEquals(3, resultList.size());

        // Verify sorting by start time
        assertEquals("Early Item", resultList.get(0).getName());
        assertEquals(LocalTime.of(9, 0), resultList.get(0).getStartTime());

        assertEquals("Middle Item", resultList.get(1).getName());
        assertEquals(LocalTime.of(12, 0), resultList.get(1).getStartTime());

        assertEquals("Late Item", resultList.get(2).getName());
        assertEquals(LocalTime.of(14, 0), resultList.get(2).getStartTime());
    }

    @Test
    @DisplayName("Should filter out deleted agenda items")
    void getAgenda_WithDeletedAgendaItems_FiltersOutDeleted() {
        AgendaItem activeItem = new AgendaItem();
        activeItem.setId(1);
        activeItem.setName("Active Item");
        activeItem.setStartTime(LocalTime.of(10, 0));
        activeItem.setDeleted(false);

        AgendaItem deletedItem = new AgendaItem();
        deletedItem.setId(2);
        deletedItem.setName("Deleted Item");
        deletedItem.setStartTime(LocalTime.of(9, 0));
        deletedItem.setDeleted(true);

        fullEvent.getAgenda().clear();
        fullEvent.getAgenda().add(activeItem);
        fullEvent.getAgenda().add(deletedItem);

        GetAgendaItemDTO activeDto = new GetAgendaItemDTO();
        activeDto.setId(1);
        activeDto.setName("Active Item");

        when(eventRepository.findById(1)).thenReturn(Optional.of(fullEvent));
        when(modelMapper.map(activeItem, GetAgendaItemDTO.class)).thenReturn(activeDto);

        Collection<GetAgendaItemDTO> result = eventService.getAgenda(1);

        assertEquals(1, result.size());
        GetAgendaItemDTO returnedItem = result.iterator().next();
        assertEquals("Active Item", returnedItem.getName());

        // Verify modelMapper was only called for non-deleted item
        verify(modelMapper, times(1)).map(activeItem, GetAgendaItemDTO.class);
        verify(modelMapper, never()).map(deletedItem, GetAgendaItemDTO.class);
    }

    @Test
    @DisplayName("Should return empty collection when all agenda items are deleted")
    void getAgenda_WhenAllAgendaItemsDeleted_ReturnsEmptyCollection() {
        AgendaItem deletedItem1 = new AgendaItem();
        deletedItem1.setId(1);
        deletedItem1.setDeleted(true);

        AgendaItem deletedItem2 = new AgendaItem();
        deletedItem2.setId(2);
        deletedItem2.setDeleted(true);

        fullEvent.getAgenda().clear();
        fullEvent.getAgenda().add(deletedItem1);
        fullEvent.getAgenda().add(deletedItem2);

        when(eventRepository.findById(1)).thenReturn(Optional.of(fullEvent));

        Collection<GetAgendaItemDTO> result = eventService.getAgenda(1);

        assertTrue(result.isEmpty());
        verify(modelMapper, never()).map(any(), eq(GetAgendaItemDTO.class));
    }

    @Test
    @DisplayName("Should map agenda items to DTOs correctly")
    void getAgenda_WithValidAgendaItems_MapsToCorrectDTOs() {
        fullEvent.getAgenda().clear();
        fullEvent.getAgenda().add(createdAgendaItem);

        GetAgendaItemDTO expectedDto = new GetAgendaItemDTO();
        expectedDto.setId(1);
        expectedDto.setName("Opening Ceremony");
        expectedDto.setDescription("Welcome speech and overview of the event.");
        expectedDto.setLocation("Main Hall");
        expectedDto.setStartTime(LocalTime.of(9, 0));
        expectedDto.setEndTime(LocalTime.of(10, 0));

        when(eventRepository.findById(1)).thenReturn(Optional.of(fullEvent));
        when(modelMapper.map(createdAgendaItem, GetAgendaItemDTO.class)).thenReturn(expectedDto);

        Collection<GetAgendaItemDTO> result = eventService.getAgenda(1);

        assertEquals(1, result.size());
        verify(modelMapper, times(1)).map(createdAgendaItem, GetAgendaItemDTO.class);

        GetAgendaItemDTO returnedDto = result.iterator().next();
        assertEquals(expectedDto, returnedDto);
    }

    public static boolean compareEvents(Event e1, Event e2) {
        if (e1 == e2) return true;
        if (e1 == null || e2 == null) return false;

        if (e1.getId() != e2.getId()) return false;
        if (e1.getMaxParticipants() != e2.getMaxParticipants()) return false;
        if (e1.isOpen() != e2.isOpen()) return false;
        if (e1.isDeleted() != e2.isDeleted()) return false;

        if (!e1.getName().equals(e2.getName())) return false;
        if (!e1.getDescription().equals(e2.getDescription())) return false;
        if (!e1.getDate().equals(e2.getDate())) return false;

        if ((e1.getOrganizer() != null && e2.getOrganizer() != null &&
                e1.getOrganizer().getId() != e2.getOrganizer().getId()) ||
                (e1.getOrganizer() == null ^ e2.getOrganizer() == null)) return false;

        if ((e1.getEventType() != null && e2.getEventType() != null &&
                e1.getEventType().getId() != e2.getEventType().getId()) ||
                (e1.getEventType() == null ^ e2.getEventType() == null)) return false;

        if ((e1.getLocation() != null && e2.getLocation() != null &&
                e1.getLocation().getId() != e2.getLocation().getId()) ||
                (e1.getLocation() == null ^ e2.getLocation() == null)) return false;

        return true;
    }

    public static boolean compareEventStats(EventStats s1, EventStats s2) {
        if (s1 == s2) return true;
        if (s1 == null || s2 == null) return false;

        if (s1.getId() != s2.getId()) return false;
        if (s1.getOneStarCount() != s2.getOneStarCount()) return false;
        if (s1.getTwoStarCount() != s2.getTwoStarCount()) return false;
        if (s1.getThreeStarCount() != s2.getThreeStarCount()) return false;
        if (s1.getFourStarCount() != s2.getFourStarCount()) return false;
        if (s1.getFiveStarCount() != s2.getFiveStarCount()) return false;
        if (s1.getParticipantsCount() != s2.getParticipantsCount()) return false;
        if (Double.compare(s1.getAverageRating(), s2.getAverageRating()) != 0) return false;

        return true;
    }

    public static boolean compareAgendaItems(AgendaItem a1, AgendaItem a2) {
        if (a1 == a2) return true;
        if (a1 == null || a2 == null) return false;

        if (!a1.getName().equals(a2.getName())) return false;
        if (!a1.getDescription().equals(a2.getDescription())) return false;
        if (!a1.getLocation().equals(a2.getLocation())) return false;
        if (!a1.getStartTime().equals(a2.getStartTime())) return false;
        if (!a1.getEndTime().equals(a2.getEndTime())) return false;

        return true;
    }

}
