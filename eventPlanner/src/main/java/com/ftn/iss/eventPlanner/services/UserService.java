package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.company.GetCompanyDTO;
import com.ftn.iss.eventPlanner.dto.company.UpdateCompanyDTO;
import com.ftn.iss.eventPlanner.dto.company.UpdatedCompanyDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import com.ftn.iss.eventPlanner.dto.user.*;
import com.ftn.iss.eventPlanner.exception.EmailAlreadyExistsException;
import com.ftn.iss.eventPlanner.model.*;
import com.ftn.iss.eventPlanner.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocationService locationService;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private OrganizerRepository organizerRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;


    private ModelMapper modelMapper = new ModelMapper();

    private static final int TOKEN_EXPIRATION = 24;

    @Value("${app.frontend-base-url}") private String baseUrl;

    private static final String CONFIRMATION_URL = "/activate?token=";

    public CreatedUserDTO create(CreateUserDTO userDTO, boolean roleUpgrade) {
        Account account = accountRepository.findByEmail(userDTO.getEmail());
        if(roleUpgrade){
            if(account==null)
                throw new IllegalArgumentException("Account with given email doesn't exist");
        }
        else {
            if(account!=null)
            {
                VerificationToken token = verificationTokenRepository.findByAccountId(account.getId());
                if(token!=null && token.getExpiresAt().isBefore(LocalDateTime.now())){
                    verificationTokenRepository.delete(token);
                    User user=account.getUser();
                    user.setAccount(null);
                    userRepository.save(user);
                    account.setUser(null);
                    accountRepository.save(account);
                    userRepository.deleteById(user.getId());
                    accountRepository.delete(account);
                }
                else
                    throw new EmailAlreadyExistsException("Email already exists");
            }
            account=new Account();
            account.setEmail(userDTO.getEmail());
        }
        account.setRole(userDTO.getRole());
        account.setStatus(AccountStatus.PENDING);
        if(!roleUpgrade)
            account.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        account = accountRepository.save(account);

        User user=null;
        if(userDTO.getRole()==Role.PROVIDER) {
            user=registerProvider(userDTO, account);
        } else if (userDTO.getRole()==Role.EVENT_ORGANIZER) {
            user=registerOrganizer(userDTO, account);
        }

        account.setUser(user);
        accountRepository.save(account);

        sendConfirmation(user);
        return modelMapper.map(user, CreatedUserDTO.class);
    }

    private User registerProvider(CreateUserDTO userDTO, Account account){
        Provider provider = modelMapper.map(userDTO, Provider.class);
        provider.setAccount(account);
        provider.setLocation(modelMapper.map(locationService.create(userDTO.getLocation()), Location.class));
        provider.getCompany().setLocation(modelMapper.map(locationService.create(userDTO.getLocation()), Location.class));
        provider.setCompany(companyRepository.save(provider.getCompany()));
        providerRepository.save(provider);
        return provider;
    }

    private User registerOrganizer(CreateUserDTO userDTO, Account account){
        Organizer organizer = modelMapper.map(userDTO, Organizer.class);
        organizer.setAccount(account);
        organizer.setLocation(modelMapper.map(locationService.create(userDTO.getLocation()), Location.class));
        organizerRepository.save(organizer);
        return organizer;
    }

    private void sendConfirmation(User user){
        VerificationToken token=new VerificationToken();
        token.setAccount(user.getAccount());
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(LocalDateTime.now().plusHours(TOKEN_EXPIRATION));
        token=verificationTokenRepository.save(token);
        EmailDetails emailDetails=new EmailDetails();
        emailDetails.setRecipient(user.getAccount().getEmail());
        emailDetails.setSubject("Account activation");
        emailDetails.setMsgBody("To activate your account click on the following link: "+baseUrl+CONFIRMATION_URL+token.getToken());
        emailService.sendSimpleEmail(emailDetails);
    }

    public void Activate(String token){
        VerificationToken verificationToken=verificationTokenRepository.findByToken(token);
        if(verificationToken==null)
            throw new IllegalArgumentException("Given verification token is not valid");
        if(verificationToken.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Verification token expired, please register again");
        Account account=verificationToken.getAccount();
        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
        verificationTokenRepository.delete(verificationToken);
    }

    public GetUserDTO getUserDetails(int accountId){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account with ID " + accountId + " not found"));
        GetUserDTO userDetails = new GetUserDTO();
        userDetails.setAccountId(account.getId());
        userDetails.setEmail(account.getEmail());
        userDetails.setRole(account.getRole());
        if(account.getRole() == Role.AUTHENTICATED_USER || account.getRole() == Role.ADMIN)
            return userDetails;
        User user = account.getUser();
        userDetails.setUserId(user.getId());
        userDetails.setFirstName(user.getFirstName());
        userDetails.setLastName(user.getLastName());
        userDetails.setPhoneNumber(user.getPhoneNumber());
        userDetails.setProfilePhoto(user.getProfilePhoto());
        userDetails.setLocation(modelMapper.map(user.getLocation(), GetLocationDTO.class));
        if(account.getRole() == Role.PROVIDER)
            userDetails.setCompany(modelMapper.map(((Provider) user).getCompany(), GetCompanyDTO.class));
        return userDetails;
    }
    public UpdatedUserDTO updateUser(int accountId, UpdateUserDTO updateUserDTO){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account with ID " + accountId + " not found"));
        if(account.getRole()==Role.AUTHENTICATED_USER || account.getRole()==Role.ADMIN)
            throw new IllegalArgumentException("User with given account ID is not a provider or organizer");
        User user = account.getUser();
        user.setFirstName(updateUserDTO.getFirstName());
        user.setLastName(updateUserDTO.getLastName());
        user.setPhoneNumber(updateUserDTO.getPhoneNumber());
        user.setLocation(modelMapper.map(locationService.create(updateUserDTO.getLocation()), Location.class));
        userRepository.save(user);
        return modelMapper.map(user, UpdatedUserDTO.class);
    }

    public UpdatedCompanyDTO updateCompany(int accountId, UpdateCompanyDTO updateCompanyDTO){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account with ID " + accountId + " not found"));
        if(account.getRole()!=Role.PROVIDER)
            throw new IllegalArgumentException("User with given account ID is not a provider");
        Provider provider = (Provider) account.getUser();
        Company company = provider.getCompany();
        company.setPhoneNumber(updateCompanyDTO.getPhoneNumber());
        company.setDescription(updateCompanyDTO.getDescription());
        company.setLocation(modelMapper.map(locationService.create(updateCompanyDTO.getLocation()), Location.class));
        companyRepository.save(company);
        return modelMapper.map(company, UpdatedCompanyDTO.class);
    }

    public void changePassword(int accountId, ChangePasswordDTO changePasswordDTO){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account with ID " + accountId + " not found"));
        if(!passwordEncoder.matches(changePasswordDTO.getOldPassword(), account.getPassword())){
            throw new IllegalArgumentException("Old password is incorrect");
        }
        account.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        accountRepository.save(account);
    }

    public void deactivateAccount(int accountId){
        Account account=accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account with ID " + accountId + " not found"));
        if(account.getRole()==Role.EVENT_ORGANIZER){
            Organizer organizer=(Organizer) account.getUser();
            List<Event> events = eventRepository.findByOrganizerId(organizer.getId());
            if(events.stream().anyMatch(event -> event.getDate().isAfter(LocalDate.now())))
                throw new IllegalArgumentException("Organizer has upcoming events, can't deactivate account");
        }
        //TODO if provider check upcoming reservations
        account.setStatus(AccountStatus.INACTIVE);
        accountRepository.save(account);
    }
}
