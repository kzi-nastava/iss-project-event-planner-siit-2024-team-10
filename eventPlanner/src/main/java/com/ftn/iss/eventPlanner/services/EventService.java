package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.PagedResponse;
import com.ftn.iss.eventPlanner.dto.agendaitem.*;
import com.ftn.iss.eventPlanner.dto.event.*;

import com.ftn.iss.eventPlanner.dto.eventstats.GetEventStatsDTO;
import com.ftn.iss.eventPlanner.dto.eventtype.GetEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import com.ftn.iss.eventPlanner.dto.user.GetOrganizerDTO;
import com.ftn.iss.eventPlanner.model.Event;
import com.ftn.iss.eventPlanner.model.EventType;
import com.ftn.iss.eventPlanner.model.Location;
import com.ftn.iss.eventPlanner.model.EventStats;
import com.ftn.iss.eventPlanner.model.*;
import com.ftn.iss.eventPlanner.model.specification.EventSpecification;
import com.ftn.iss.eventPlanner.repositories.*;
import com.ftn.iss.eventPlanner.util.NetworkUtils;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.io.InputStream;
import java.net.SocketException;
import java.time.LocalDate;
import java.util.*;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private LocationService locationService;
    @Autowired
    private EventTypeRepository eventTypeRepository;
    @Autowired
    private EventStatsRepository eventStatsRepository;
    @Autowired
    private OrganizerRepository organizerRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private AccountService accountService;

    private ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private AgendaItemRepository agendaItemRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private EventInviteTokenRepository eventInviteTokenRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${app.base-url}") private String baseUrl;

    private static final int TOKEN_EXPIRATION = 7;
    private final String IP_BASE_URL = "http://" + NetworkUtils.getLocalIpAddress() + ":8080/api";

    private static final String CONFIRMATION_URL = "/events/accept-invite/";

    public EventService() throws SocketException {
    }

    public List<GetEventDTO> findAll() {
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(this::mapToGetEventDTO)
                .collect(Collectors.toList());
    }
    public PagedResponse<GetEventDTO> getAllEvents(
            Pageable pageable,
            Integer eventTypeId,
            String location,
            Integer maxParticipants,
            Double minRating,
            LocalDate startDate,
            LocalDate endDate,
            String name,
            String sortBy,
            String sortDirection,
            Integer accountId
    ) {
        if (accountId != null && (location == null || location.isEmpty())) {
            Location userLocation = accountService.findUserLocation(accountId);
            if (userLocation != null) {
                location = userLocation.getCity();
            }
        }

        if (sortBy != null && !"none".equalsIgnoreCase(sortBy)) {
            String sortField = switch (sortBy.toLowerCase()) {
                case "name" -> "name";
                case "date" -> "date";
                case "averagerating" -> "stats.averageRating";
                case "location.city" -> "location.city";
                default -> null;
            };

            if (sortField != null) {
                var sortDirectionEnum = "desc".equalsIgnoreCase(sortDirection)
                        ? org.springframework.data.domain.Sort.Direction.DESC
                        : org.springframework.data.domain.Sort.Direction.ASC;

                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        org.springframework.data.domain.Sort.by(sortDirectionEnum, sortField));
            }
        }

        Specification<Event> specification = Specification.where(EventSpecification.hasEventTypeId(eventTypeId))
                .and(EventSpecification.hasLocation(location))
                .and(EventSpecification.maxParticipants(maxParticipants))
                .and(EventSpecification.betweenDates(startDate, endDate))
                .and(EventSpecification.minAverageRating(minRating))
                .and(EventSpecification.hasName(name));

        Page<Event> pagedEvents = eventRepository.findAll(specification, pageable);

        List<GetEventDTO> eventDTOs = pagedEvents.getContent().stream()
                .map(this::mapToGetEventDTO)
                .collect(Collectors.toList());

        return new PagedResponse<>(eventDTOs, pagedEvents.getTotalPages(), pagedEvents.getTotalElements());
    }
    public List<GetEventDTO> findEventsByOrganizer(Integer accountId){
        List<Event> events = eventRepository.findAll();
        List<GetEventDTO> eventDTOs = new ArrayList<>();
        if (accountId != null) {
            eventDTOs = events.stream()
                    .filter(event -> event.getOrganizer().getAccount().getId() == accountId)
                    .map(this::mapToGetEventDTO)
                    .collect(Collectors.toList());
        }
        return eventDTOs;
    }

    public List<GetEventDTO> findTopEvents(Integer accountId) {
        List<Event> events = eventRepository.findAll();

        if (accountId != null) {
            Location userLocation = accountService.findUserLocation(accountId);

            if (userLocation != null) {
                events = events.stream()
                        .filter(event -> event.getLocation() != null &&
                                event.getLocation().getCity().equalsIgnoreCase(userLocation.getCity()))
                        .collect(Collectors.toList());
            }
        }

        return events.stream()
                .filter(Event::isOpen)
                .sorted((e1, e2) -> e2.getDateCreated().compareTo(e1.getDateCreated()))
                .limit(5)
                .map(this::mapToGetEventDTO)
                .collect(Collectors.toList());
    }

    public CreatedEventDTO create (CreateEventDTO createEventDTO){
        Event event = modelMapper.map(createEventDTO, Event.class);
        if(event.getDate().isBefore(LocalDate.now())){
            throw new IllegalArgumentException("Event date must be in the future");
        }
        event.setId(0);
        Location location = modelMapper.map(locationService.create(createEventDTO.getLocation()), Location.class);
        event.setLocation(location);
        EventType eventType = null;
        if(createEventDTO.getEventTypeId()!=0) {
            eventType=eventTypeRepository.findById(createEventDTO.getEventTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Event Type with ID " + createEventDTO.getEventTypeId() + " not found"));
        }
        event.setEventType(eventType);
        event.setStats(eventStatsRepository.save(new EventStats()));
        event.setOrganizer(organizerRepository.findById(createEventDTO.getOrganizerId())
                .orElseThrow(() -> new IllegalArgumentException("Organizer with ID " + createEventDTO.getOrganizerId() + " not found")));
        event.setDeleted(false);
        event.setDateCreated(java.time.LocalDateTime.now());
        event = eventRepository.save(event);
        eventRepository.flush();
        return modelMapper.map(event, CreatedEventDTO.class);
    }

    public UpdatedEventDTO update (int eventId, UpdateEventDTO updateEventDTO){
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " not found"));
        event.setName(updateEventDTO.getName());
        event.setDescription(updateEventDTO.getDescription());
        event.setOpen(updateEventDTO.isOpen());
        //TODO: check invitations if publicity is changed
        if(updateEventDTO.getMaxParticipants() < event.getStats().getParticipantsCount()) {
            throw new IllegalArgumentException("Max participants cannot be less than current participants count");
        }
        if(updateEventDTO.getMaxParticipants() < event.getGuestList().size()) {
            throw new IllegalArgumentException("Max participants cannot be less than current guest list size");
        }
        event.setMaxParticipants(updateEventDTO.getMaxParticipants());
        checkDateUpdate(event, updateEventDTO.getDate());
        event.setDate(updateEventDTO.getDate());
        Location location = modelMapper.map(locationService.create(updateEventDTO.getLocation()), Location.class);
        event.setLocation(location);
        EventType eventType = null;
        if(updateEventDTO.getEventTypeId()!=0) {
            eventType = eventTypeRepository.findById(updateEventDTO.getEventTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Event Type with ID " + updateEventDTO.getEventTypeId() + " not found"));
        }
        event.setEventType(eventType);
        event = eventRepository.save(event);
        eventRepository.flush();
        return modelMapper.map(event, UpdatedEventDTO.class);
    }

    private void checkDateUpdate(Event event, LocalDate date){
        if(event.getDate().isEqual(date))
            return;
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Event date must be in the future");
        }
        reservationRepository.findByEventId(event.getId()).stream()
                .filter(r -> r.getStatus() == Status.ACCEPTED || r.getStatus() == Status.PENDING)
                .findAny()
                .ifPresent(r -> { throw new IllegalArgumentException("Event date can't be changed when it has reservations"); });
    }

    public Collection<GetAgendaItemDTO> getAgenda(int eventId){
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " not found"));
        return event.getAgenda().stream()
                .filter(agendaItem -> !agendaItem.isDeleted())
                .sorted(Comparator.comparing(AgendaItem::getStartTime))
                .map(agendaItem -> modelMapper.map(agendaItem, GetAgendaItemDTO.class))
                .collect(Collectors.toList());
    }

    public GetEventDTO getEvent(int eventId){
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " not found"));
        return mapToGetEventDTO(event);
    }

    public CreatedEventRatingDTO rateEvent(int eventId, int rating){
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " not found"));
        EventStats stats = event.getStats();
        switch (rating) {
            case 1:
                stats.setOneStarCount(stats.getOneStarCount()+1);
                break;
            case 2:
                stats.setTwoStarCount(stats.getTwoStarCount()+1);
                break;
            case 3:
                stats.setThreeStarCount(stats.getThreeStarCount()+1);
                break;
            case 4:
                stats.setFourStarCount(stats.getFourStarCount()+1);
                break;
            case 5:
                stats.setFiveStarCount(stats.getFiveStarCount()+1);
                break;
            default:
                throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        eventStatsRepository.save(stats);
        return new CreatedEventRatingDTO(stats.getAverageRating());
    }

    public CreatedAgendaItemDTO createAgendaItem(int eventId, CreateAgendaItemDTO agendaItemDto){
        if(agendaItemDto.getStartTime().isAfter(agendaItemDto.getEndTime())){
            throw new IllegalArgumentException("Start time must be before end time");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " not found"));
        AgendaItem agendaItem = modelMapper.map(agendaItemDto, AgendaItem.class);
        agendaItemRepository.save(agendaItem);
        event.getAgenda().add(agendaItem);
        eventRepository.save(event);
        return modelMapper.map(agendaItem, CreatedAgendaItemDTO.class);
    }

    public UpdatedAgendaItemDTO updateAgendaItem(int eventId, int agendaItemId, UpdateAgendaItemDTO agendaItemDto){
        if(agendaItemDto.getStartTime().isAfter(agendaItemDto.getEndTime())){
            throw new IllegalArgumentException("Start time must be before end time");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " not found"));
        AgendaItem agendaItem = agendaItemRepository.findById(agendaItemId)
                .orElseThrow(() -> new NotFoundException("Agenda Item with ID " + agendaItemId + " not found"));
        if(!event.getAgenda().stream().anyMatch(agendaItem1 -> agendaItem1.getId() == agendaItemId)){
            throw new IllegalArgumentException("Agenda Item with ID " + agendaItemId + " is not part of Event with ID " + eventId);
        }
        agendaItem.setName(agendaItemDto.getName());
        agendaItem.setDescription(agendaItemDto.getDescription());
        agendaItem.setLocation(agendaItemDto.getLocation());
        agendaItem.setStartTime(agendaItemDto.getStartTime());
        agendaItem.setEndTime(agendaItemDto.getEndTime());
        agendaItemRepository.save(agendaItem);
        return modelMapper.map(agendaItem, UpdatedAgendaItemDTO.class);
    }

    public void deleteAgendaItem(int eventId, int agendaItemId){
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " not found"));
        AgendaItem agendaItem = agendaItemRepository.findById(agendaItemId)
                .orElseThrow(() -> new NotFoundException("Agenda Item with ID " + agendaItemId + " not found"));
        if(!event.getAgenda().stream().anyMatch(agendaItem1 -> agendaItem1.getId() == agendaItemId)){
            throw new IllegalArgumentException("Agenda Item with ID " + agendaItemId + " is not part of Event with ID " + eventId);
        }
        agendaItem.setDeleted(true);
        agendaItemRepository.save(agendaItem);
    }

    public GetEventStatsDTO getEventStats(int eventId){
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " not found"));
        EventStats stats = event.getStats();
        GetEventStatsDTO statsDTO = modelMapper.map(stats, GetEventStatsDTO.class);
        statsDTO.setEventName(event.getName());
        return statsDTO;
    }

    public GetGuestsDTO getGuestList(int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " not found"));
        GetGuestsDTO dto = new GetGuestsDTO();
        dto.setGuests(event.getGuestList());
        return dto;
    }

    public byte[] generateOpenEventReport(int eventId) throws JRException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " not found"));
        if(!event.isOpen())
            throw new IllegalArgumentException("Event with ID " + eventId + " is not open");
        EventStats eventStats=event.getStats();
        // Load the JRXML template from classpath
        String reportPath = "template/open_event_report.jrxml";  // Ensure the path is correct
        InputStream reportStream = getClass().getClassLoader().getResourceAsStream(reportPath);
        if (reportStream == null) {
            throw new JRException("Could not find report template: " + reportPath);
        }

        // Compile the Jasper report
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        // Prepare the data for the report
        List<HashMap<String, Object>> reportData = new ArrayList<>();
        HashMap<String, Object> data = new HashMap<>();
        data.put("participants", eventStats.getParticipantsCount());
        data.put("rating1Count", eventStats.getOneStarCount());
        data.put("rating2Count", eventStats.getTwoStarCount());
        data.put("rating3Count", eventStats.getThreeStarCount());
        data.put("rating4Count", eventStats.getFourStarCount());
        data.put("rating5Count", eventStats.getFiveStarCount());
        reportData.add(data);

        // Convert data into JRDataSource
        JRDataSource jrDataSource = new JRBeanCollectionDataSource(reportData);

        // Fill the report with data
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("eventName", event.getName()); // Replace with actual event name
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrDataSource);

        // Export to PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    public byte[] generateEventInfoReport(int eventId) throws JRException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " not found"));

        String reportPath = "template/event_report.jrxml";
        String agendaReportPath = "template/agenda_subreport.jrxml";

        InputStream reportStream = getClass().getClassLoader().getResourceAsStream(reportPath);
        InputStream agendaReportStream = getClass().getClassLoader().getResourceAsStream(agendaReportPath);

        if (reportStream == null || agendaReportStream == null) {
            throw new JRException("Could not find report templates");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
        JasperReport agendaSubreport = JasperCompileManager.compileReport(agendaReportStream);

        List<HashMap<String, Object>> reportData = new ArrayList<>();
        HashMap<String, Object> data = new HashMap<>();

        data.put("eventName", event.getName());
        data.put("eventType", event.getEventType().getName());
        data.put("description", event.getDescription());
        data.put("location", event.getLocation().toString());
        data.put("eventDate", event.getDate().toString());
        data.put("participants", event.getStats().getParticipantsCount());

        Organizer organizer = event.getOrganizer();
        data.put("organizerName", organizer.getFirstName()+" "+organizer.getLastName());
        data.put("organizerLocation", organizer.getLocation().toString());
        data.put("organizerEmail", organizer.getAccount().getEmail());
        data.put("organizerPhone", organizer.getPhoneNumber());

        List<HashMap<String, Object>> agendaItems = new ArrayList<>();
        for (AgendaItem item : event.getAgenda().stream().filter(agendaItem -> !agendaItem.isDeleted()).sorted(Comparator.comparing(AgendaItem::getStartTime)).collect(Collectors.toList())) {
            HashMap<String, Object> agendaItem = new HashMap<>();
            agendaItem.put("itemName", item.getName());
            agendaItem.put("itemDescription", item.getDescription());
            agendaItem.put("startTime", item.getStartTime().toString());
            agendaItem.put("endTime", item.getEndTime().toString());
            agendaItem.put("itemLocation", item.getLocation());
            agendaItems.add(agendaItem);
        }
        data.put("agendaItems", agendaItems);

        reportData.add(data);
        JRDataSource jrDataSource = new JRBeanCollectionDataSource(reportData);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("AgendaSubreport", agendaSubreport);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrDataSource);

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    public GetEventStatsDTO addParticipant(int eventId){
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " not found"));
        EventStats stats = event.getStats();
        if(stats.getParticipantsCount()>=event.getMaxParticipants()){
            throw new IllegalArgumentException("Event is full");
        }
        stats.setParticipantsCount(stats.getParticipantsCount()+1);
        eventStatsRepository.save(stats);
        GetEventStatsDTO statsDTO = modelMapper.map(stats, GetEventStatsDTO.class);
        statsDTO.setEventName(event.getName());
        return statsDTO;
    }

    public void sendInvitations(int eventId, CreateGuestListDTO emails){
        for (String email : emails.getGuests()) {
            inviteGuest(eventId, email);
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found."));

        EventStats stats = event.getStats();
        if(stats.getParticipantsCount()>=event.getMaxParticipants()){
            throw new IllegalArgumentException("Event is full");
        }
        stats.setParticipantsCount(event.getGuestList().size());
        eventStatsRepository.save(stats);
    }

    @Transactional
    public void inviteGuest(int eventId, String guestEmail) {
        Account account = accountRepository.findByEmail(guestEmail);
        String password = null;

        if (account == null) {
            password = UUID.randomUUID().toString().substring(0, 10);
            account = new Account();
            account.setEmail(guestEmail);
            account.setPassword(passwordEncoder.encode(password));
            account.setStatus(AccountStatus.ACTIVE);
            account.setRole(Role.AUTHENTICATED_USER);
            accountRepository.save(account);
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found."));

        EventInviteToken token = new EventInviteToken();
        token.setEmail(guestEmail);
        token.setEvent(event);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(LocalDateTime.now().plusDays(TOKEN_EXPIRATION));
        eventInviteTokenRepository.save(token);

        String inviteLink = IP_BASE_URL + CONFIRMATION_URL + token.getToken();

        String message = "You're invited to the event: " + event.getName() +
                "\n\n📅 Date: " + event.getDate() +
                "\n📍 Location: " + event.getLocation().getStreet() + " " + event.getLocation().getHouseNumber() + ", " +
                event.getLocation().getCity() + ", " + event.getLocation().getCountry() +
                "\n\n📝 Description: " + event.getDescription() +
                "\n\n👉 Click here to participate: " + inviteLink;

        if (password != null) {
            message += "\n\n🔑 Your generated password: " + password;
        }

        emailService.sendSimpleEmail(new EmailDetails(guestEmail, message, "Event Invitation", ""));

        if (!event.getGuestList().contains(account.getEmail())) {
            event.getGuestList().add(account.getEmail());
            eventRepository.save(event);
        }
    }

    @Transactional
    public void processInvitation(String token, GetGuestDTO guestDTO) {
        EventInviteToken invitation = eventInviteTokenRepository.findByToken(token);
        if (invitation == null || invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invitation is invalid or expired");
        }

        if (!invitation.getEmail().equals(guestDTO.getEmail())) {
            throw new IllegalArgumentException("You are not the intended recipient of this invitation.");
        }

        Account account = accountRepository.findByEmail(invitation.getEmail());
        if (account == null) {
            throw new NotFoundException("Account does not exist for this invitation.");
        }

        Event event = eventRepository.findById(invitation.getEvent().getId())
                .orElseThrow(() -> new NotFoundException("Event not found."));

        if (!account.getAcceptedEvents().contains(event)){
            account.getAcceptedEvents().add(event);
            accountRepository.save(account);
        }

        eventInviteTokenRepository.delete(invitation);
    }

    // HELPER FUNCTIONS

    private GetEventDTO mapToGetEventDTO(Event event) {
        GetEventDTO dto = new GetEventDTO();

        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDate(event.getDate());
        dto.setOrganizer(setGetOrganizerDTO(event));
        if(event.getEventType()!=null)
            dto.setEventType(modelMapper.map(event.getEventType(), GetEventTypeDTO.class));

        if (event.getLocation() != null) {
            GetLocationDTO locationDTO = setGetLocationDTO(event);
            dto.setLocation(locationDTO);
        }

        dto.setMaxParticipants(event.getMaxParticipants());
        if (event.getStats()!=null) {
            dto.setAverageRating(event.getStats().getAverageRating());
            dto.setParticipantsCount(event.getStats().getParticipantsCount());
        }else{
            dto.setAverageRating(0);
        }
        dto.setDescription(event.getDescription());
        dto.setOpen(event.isOpen());
        return dto;
    }

    private GetLocationDTO setGetLocationDTO (Event event){
        GetLocationDTO locationDTO = new GetLocationDTO();
        locationDTO.setCountry(event.getLocation().getCountry());
        locationDTO.setCity(event.getLocation().getCity());
        locationDTO.setStreet(event.getLocation().getStreet());
        locationDTO.setHouseNumber(event.getLocation().getHouseNumber());
        return locationDTO;
    }

    private GetOrganizerDTO setGetOrganizerDTO(Event event){
        GetOrganizerDTO organizerDTO = new GetOrganizerDTO();
        organizerDTO.setId(event.getOrganizer().getId());
        organizerDTO.setEmail(event.getOrganizer().getAccount().getEmail());
        organizerDTO.setFirstName(event.getOrganizer().getFirstName());
        organizerDTO.setLastName(event.getOrganizer().getLastName());
        organizerDTO.setPhoneNumber(event.getOrganizer().getPhoneNumber());
        organizerDTO.setProfilePhoto(event.getOrganizer().getProfilePhoto());
        organizerDTO.setLocation(modelMapper.map(event.getOrganizer().getLocation(), GetLocationDTO.class));
        organizerDTO.setAccountId(event.getOrganizer().getAccount().getId());
        return organizerDTO;
    }


    private Comparator<Event> getEventComparator(String sortBy, String sortDirection) {
        if (sortBy == null || "none".equalsIgnoreCase(sortBy)) {
            return null;
        }

        Comparator<Event> comparator = switch (sortBy.toLowerCase()) {
            case "name" -> Comparator.comparing(Event::getName, String.CASE_INSENSITIVE_ORDER);
            case "date" -> Comparator.comparing(Event::getDate);
            case "averagerating" -> Comparator.comparing(event -> event.getStats() != null
                    ? event.getStats().getAverageRating() : 0.0);
            case "location.city" -> Comparator.comparing(event -> event.getLocation().getCity(), String.CASE_INSENSITIVE_ORDER);
            default -> null;
        };

        if (comparator != null && "desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

}