package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.PagedResponse;
import com.ftn.iss.eventPlanner.dto.agendaitem.*;
import com.ftn.iss.eventPlanner.dto.event.CreateEventDTO;
import com.ftn.iss.eventPlanner.dto.event.CreatedEventDTO;

import com.ftn.iss.eventPlanner.dto.event.CreatedEventRatingDTO;
import com.ftn.iss.eventPlanner.dto.event.GetEventDTO;
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
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.webjars.NotFoundException;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.Comparator;
import java.util.stream.Collectors;

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

    private ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private AgendaItemRepository agendaItemRepository;

    public List<GetEventDTO> findAll() {
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(this::mapToGetEventDTO)
                .collect(Collectors.toList());
    }

    public List<GetEventDTO> getAllEvents(
            Integer eventTypeId,
            String location,
            Integer maxParticipants,
            Double minRating,
            LocalDate startDate,
            LocalDate endDate,
            String name
    ) {
        Specification<Event> specification = Specification.where(EventSpecification.hasEventTypeId(eventTypeId))
                .and(EventSpecification.hasLocation(location))
                .and(EventSpecification.maxParticipants(maxParticipants))
                .and(EventSpecification.betweenDates(startDate, endDate))
                .and(EventSpecification.hasName(name));

        List<Event> events = eventRepository.findAll(specification);

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
            String sortDirection
    ) {
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





    public List<GetEventDTO> findTopEvents() {
        List<Event> events = eventRepository.findAll();

        return events.stream()
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
