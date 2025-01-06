package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.agendaitem.GetAgendaItemDTO;
import com.ftn.iss.eventPlanner.dto.event.GetEventDTO;
import com.ftn.iss.eventPlanner.dto.offering.GetOfferingDTO;
import com.ftn.iss.eventPlanner.model.Account;
import com.ftn.iss.eventPlanner.model.AccountStatus;
import com.ftn.iss.eventPlanner.model.Event;
import com.ftn.iss.eventPlanner.model.Offering;
import com.ftn.iss.eventPlanner.repositories.AccountRepository;
import com.ftn.iss.eventPlanner.repositories.EventRepository;
import com.ftn.iss.eventPlanner.repositories.OfferingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OfferingRepository offeringRepository;

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
    public Collection<GetEventDTO> getFavouriteEvents(int accountId) {
        Account account = accountRepository.findByIdWithFavouriteEvents(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        return account.getFavouriteEvents().stream()
                .map(event -> modelMapper.map(event, GetEventDTO.class))
                .collect(Collectors.toList());
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
    public Collection<GetOfferingDTO> getFavouriteOfferings(int accountId) {
        Account account = accountRepository.findByIdWithFavouriteOfferings(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        return account.getFavouriteOfferings().stream()
                .map(offering -> modelMapper.map(offering, GetOfferingDTO.class))
                .collect(Collectors.toList());
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
}