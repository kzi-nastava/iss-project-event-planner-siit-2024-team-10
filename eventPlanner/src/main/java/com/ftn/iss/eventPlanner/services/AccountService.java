package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.PagedResponse;
import com.ftn.iss.eventPlanner.dto.agendaitem.GetAgendaItemDTO;
import com.ftn.iss.eventPlanner.dto.comment.GetCommentDTO;
import com.ftn.iss.eventPlanner.dto.company.GetCompanyDTO;
import com.ftn.iss.eventPlanner.dto.event.GetEventDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import com.ftn.iss.eventPlanner.dto.offering.GetOfferingDTO;
import com.ftn.iss.eventPlanner.dto.offeringcategory.GetOfferingCategoryDTO;
import com.ftn.iss.eventPlanner.dto.user.GetProviderDTO;
import com.ftn.iss.eventPlanner.model.*;
import com.ftn.iss.eventPlanner.repositories.AccountRepository;
import com.ftn.iss.eventPlanner.repositories.EventRepository;
import com.ftn.iss.eventPlanner.repositories.OfferingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OfferingRepository offeringRepository;
    @Autowired
    private OfferingService offeringService;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EventRepository eventRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(username);
        if (account == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            return account;
        }
    }
    @Transactional
    public PagedResponse<GetEventDTO> getFavouriteEvents(int accountId, Pageable pageable) {
        Account account = accountRepository.findByIdWithFavouriteEvents(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        List<GetEventDTO> eventList = account.getFavouriteEvents().stream()
                .map(event -> modelMapper.map(event, GetEventDTO.class))
                .sorted(Comparator.comparing(GetEventDTO::getName))
                .collect(Collectors.toList());
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), eventList.size());

        List<GetEventDTO> pagedEvents = start > eventList.size() ?
                Collections.emptyList() : eventList.subList(start, end);

        Page<GetEventDTO> eventPage = new PageImpl<>(pagedEvents, pageable, eventList.size());

        return new PagedResponse<>(pagedEvents, eventPage.getTotalPages(), eventPage.getTotalElements());
    }

    public GetEventDTO getFavouriteEvent(int accountId, int eventId){
        Account account = accountRepository.findByIdWithFavouriteEvents(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        Event event = account.getFavouriteEvents().stream().filter(e -> e.getId() == eventId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Event not found in favourites"));
        return modelMapper.map(event, GetEventDTO.class);
    }

    @Transactional
    public void addEventToFavourites(int accountId, int eventId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException("Account not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        account.getFavouriteEvents().add(event);
        accountRepository.save(account);
    }
    @Transactional
    public void removeEventFromFavourites(int accountId, int eventId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException("Account not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        account.getFavouriteEvents().removeIf(e -> e.getId() == eventId);
        accountRepository.save(account);
    }

    @Transactional
    public PagedResponse<GetOfferingDTO> getFavouriteOfferings(int accountId, Pageable pageable) {
        Account account = accountRepository.findByIdWithFavouriteOfferings(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        List<GetOfferingDTO> offeringList = account.getFavouriteOfferings().stream()
                .map(this::mapToGetOfferingDTO)
                .sorted(Comparator.comparing(GetOfferingDTO::getName))
                .collect(Collectors.toList());
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), offeringList.size());

        List<GetOfferingDTO> pagedOfferings = start > offeringList.size() ?
                Collections.emptyList() : offeringList.subList(start, end);

        Page<GetOfferingDTO> offeringPage = new PageImpl<>(pagedOfferings, pageable, offeringList.size());

        return new PagedResponse<>(pagedOfferings, offeringPage.getTotalPages(), offeringPage.getTotalElements());
    }

    public GetOfferingDTO getFavouriteOffering(int accountId, int offeringId) {
        Account account = accountRepository.findByIdWithFavouriteOfferings(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        Offering offering = account.getFavouriteOfferings().stream()
                .filter(o -> o.getId() == offeringId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Offering not found in favourites"));
        return mapToGetOfferingDTO(offering);
    }

    @Transactional
    public void addOfferingToFavourites(int accountId, int offeringId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException("Account not found"));
        Offering offering = offeringRepository.findById(offeringId).orElseThrow(() -> new NotFoundException("Offering not found"));
        account.getFavouriteOfferings().add(offering);
        accountRepository.save(account);
    }
    @Transactional
    public void removeOfferingFromFavourites(int accountId, int offeringId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException("Account not found"));
        account.getFavouriteOfferings().removeIf(e -> e.getId() == offeringId);
        accountRepository.save(account);
    }

    public Location findUserLocation(int accountId){
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException("Account not found"));
        if (account.getRole().equals(Role.PROVIDER)||account.getRole().equals(Role.EVENT_ORGANIZER)) {
            return account.getUser().getLocation();
        }
        return null;
    }

    private GetOfferingDTO mapToGetOfferingDTO(Offering offering) {
        GetOfferingDTO dto = new GetOfferingDTO();

        dto.setId(offering.getId());
        dto.setProvider(setGetProviderDTO(offering));
        dto.setCategory(modelMapper.map(offering.getCategory(), GetOfferingCategoryDTO.class));
        dto.setAverageRating(calculateAverageRating(offering));
        if (offering.getClass().equals(Product.class)) {
            Product pr = (Product) offering;
            dto.setName(pr.getCurrentDetails().getName());
            dto.setDescription(pr.getCurrentDetails().getDescription());
            dto.setPrice(pr.getCurrentDetails().getPrice());
            dto.setDiscount(pr.getCurrentDetails().getDiscount());
            dto.setLocation(modelMapper.map(pr.getProvider().getLocation(), GetLocationDTO.class));
            dto.setPhotos(pr.getCurrentDetails().getPhotos());
            dto.setProduct(true);
        }
        else{
            com.ftn.iss.eventPlanner.model.Service service = (com.ftn.iss.eventPlanner.model.Service) offering;
            dto.setName(service.getCurrentDetails().getName());
            dto.setDescription(service.getCurrentDetails().getDescription());
            dto.setPrice(service.getCurrentDetails().getPrice());
            dto.setDiscount(service.getCurrentDetails().getDiscount());
            dto.setLocation(modelMapper.map(service.getProvider().getLocation(), GetLocationDTO.class));
            dto.setSpecification(service.getCurrentDetails().getSpecification());
            dto.setPhotos(service.getCurrentDetails().getPhotos());
            dto.setProduct(false);
        }
        return dto;
    }
    public double calculateAverageRating(Offering offering) {
        List<GetCommentDTO> comments = offeringService.getComments(offering.getId());
        if (comments == null || comments.isEmpty()) {
            return 0.0;
        }

        OptionalDouble average = comments.stream()
                .mapToInt(GetCommentDTO::getRating)
                .average();

        return average.orElse(0.0);
    }

    private GetProviderDTO setGetProviderDTO(Offering offering){
        GetProviderDTO providerDTO = new GetProviderDTO();
        providerDTO.setId(offering.getProvider().getId());
        providerDTO.setEmail(offering.getProvider().getAccount().getEmail());
        providerDTO.setFirstName(offering.getProvider().getFirstName());
        providerDTO.setLastName(offering.getProvider().getLastName());
        providerDTO.setPhoneNumber(offering.getProvider().getPhoneNumber());
        providerDTO.setProfilePhoto(offering.getProvider().getProfilePhoto());
        providerDTO.setLocation(modelMapper.map(offering.getProvider().getLocation(), GetLocationDTO.class));
        providerDTO.setCompany(setGetCompanyDTO(offering));
        providerDTO.setAccountId(offering.getProvider().getAccount().getId());
        return providerDTO;
    }

    private GetCompanyDTO setGetCompanyDTO(Offering offering){
        GetCompanyDTO companyDTO = new GetCompanyDTO();
        companyDTO.setName(offering.getProvider().getCompany().getName());
        companyDTO.setEmail(offering.getProvider().getAccount().getEmail());
        companyDTO.setDescription(offering.getProvider().getCompany().getDescription());
        companyDTO.setPhoneNumber(offering.getProvider().getCompany().getPhoneNumber());
        companyDTO.setPhotos(offering.getProvider().getCompany().getPhotos());
        companyDTO.setLocation(modelMapper.map(offering.getProvider().getCompany().getLocation(), GetLocationDTO.class));
        companyDTO.setPhoneNumber(offering.getProvider().getCompany().getPhoneNumber());

        return companyDTO;
    }
}