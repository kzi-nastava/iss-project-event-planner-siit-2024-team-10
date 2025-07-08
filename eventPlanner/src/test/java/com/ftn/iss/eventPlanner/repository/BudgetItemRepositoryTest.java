package com.ftn.iss.eventPlanner.repository;

import com.ftn.iss.eventPlanner.model.*;
import com.ftn.iss.eventPlanner.repositories.BudgetItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;
import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // add this because of jdk 21, h2 base cant activate
public class BudgetItemRepositoryTest {

    @Autowired
    private BudgetItemRepository budgetItemRepository;

    @Autowired
    private TestEntityManager entityManager;

    private BudgetItem testBudgetItem1;
    private BudgetItem testBudgetItem2;
    private BudgetItem testBudgetItem3;
    private Event testEvent1;
    private Event testEvent2;
    private OfferingCategory testCategory1;
    private OfferingCategory testCategory2;
    private Organizer testOrganizer;
    private EventType testEventType;
    private Location testLocation;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        // Setup Location
        testLocation = new Location();
        testLocation.setCity("Belgrade");
        testLocation.setCountry("Serbia");
        testLocation.setStreet("Knez Mihailova");
        testLocation.setHouseNumber("1");
        testLocation = entityManager.persistAndFlush(testLocation);

        // Setup Account
        testAccount = new Account();
        testAccount.setEmail("test@example.com");
        testAccount.setPassword("password123");
        testAccount.setRole(Role.EVENT_ORGANIZER);
        testAccount.setNotificationsSilenced(false);
        testAccount.setLastPasswordResetDate(new Timestamp(System.currentTimeMillis()));
        testAccount.setStatus(AccountStatus.ACTIVE);
        testAccount = entityManager.persistAndFlush(testAccount);

        // Setup Organizer
        testOrganizer = new Organizer();
        testOrganizer.setFirstName("John");
        testOrganizer.setLastName("Doe");
        testOrganizer.setPhoneNumber("123456789");
        testOrganizer.setLocation(testLocation);
        testOrganizer.setAccount(testAccount);
        testOrganizer = entityManager.persistAndFlush(testOrganizer);

        // Setup EventType
        testEventType = new EventType();
        testEventType.setName("Conference");
        testEventType.setDescription("Professional conference");
        testEventType.setActive(true);
        testEventType = entityManager.persistAndFlush(testEventType);

        // Setup OfferingCategories
        testCategory1 = new OfferingCategory();
        testCategory1.setName("Catering");
        testCategory1.setDescription("Food and beverages");
        testCategory1.setDeleted(false);
        testCategory1.setPending(false);
        testCategory1.setCreatorId(1);
        testCategory1 = entityManager.persistAndFlush(testCategory1);

        testCategory2 = new OfferingCategory();
        testCategory2.setName("Sound System");
        testCategory2.setDescription("Audio equipment");
        testCategory2.setDeleted(false);
        testCategory2.setPending(false);
        testCategory2.setCreatorId(1);
        testCategory2 = entityManager.persistAndFlush(testCategory2);

        // Setup Events
        testEvent1 = new Event();
        testEvent1.setOrganizer(testOrganizer);
        testEvent1.setEventType(testEventType);
        testEvent1.setName("Tech Conference 2024");
        testEvent1.setDescription("Annual technology conference");
        testEvent1.setMaxParticipants(100);
        testEvent1.setOpen(true);
        testEvent1.setDate(LocalDate.now().plusDays(30));
        testEvent1.setDeleted(false);
        testEvent1.setLocation(testLocation);
        testEvent1.setDateCreated(LocalDateTime.now());
        testEvent1 = entityManager.persistAndFlush(testEvent1);

        testEvent2 = new Event();
        testEvent2.setOrganizer(testOrganizer);
        testEvent2.setEventType(testEventType);
        testEvent2.setName("Business Summit");
        testEvent2.setDescription("Business networking event");
        testEvent2.setMaxParticipants(50);
        testEvent2.setOpen(true);
        testEvent2.setDate(LocalDate.now().plusDays(60));
        testEvent2.setDeleted(false);
        testEvent2.setLocation(testLocation);
        testEvent2.setDateCreated(LocalDateTime.now());
        testEvent2 = entityManager.persistAndFlush(testEvent2);

        // Setup BudgetItems
        testBudgetItem1 = new BudgetItem();
        testBudgetItem1.setAmount(1500.0);
        testBudgetItem1.setDeleted(false);
        testBudgetItem1.setCategory(testCategory1);
        testBudgetItem1.setEvent(testEvent1);
        testBudgetItem1.setServices(new HashSet<>());
        testBudgetItem1.setProducts(new HashSet<>());

        testBudgetItem2 = new BudgetItem();
        testBudgetItem2.setAmount(800.0);
        testBudgetItem2.setDeleted(false);
        testBudgetItem2.setCategory(testCategory2);
        testBudgetItem2.setEvent(testEvent1);
        testBudgetItem2.setServices(new HashSet<>());
        testBudgetItem2.setProducts(new HashSet<>());

        testBudgetItem3 = new BudgetItem();
        testBudgetItem3.setAmount(2000.0);
        testBudgetItem3.setDeleted(true);
        testBudgetItem3.setCategory(testCategory1);
        testBudgetItem3.setEvent(testEvent2);
        testBudgetItem3.setServices(new HashSet<>());
        testBudgetItem3.setProducts(new HashSet<>());
    }

    @Test
    @DisplayName("Should save budget item successfully")
    public void shouldSaveBudgetItem() {
        // When
        BudgetItem savedBudgetItem = budgetItemRepository.save(testBudgetItem1);

        // Then
        assertThat(savedBudgetItem).isNotNull();
        assertThat(savedBudgetItem.getId()).isNotNull();
        assertThat(savedBudgetItem.getAmount()).isEqualTo(1500.0);
        assertThat(savedBudgetItem.isDeleted()).isFalse();
        assertThat(savedBudgetItem.getCategory()).isEqualTo(testCategory1);
        assertThat(savedBudgetItem.getEvent()).isEqualTo(testEvent1);
    }

    @Test
    @DisplayName("Should find budget item by ID")
    public void shouldFindBudgetItemById() {
        // Given
        BudgetItem savedBudgetItem = budgetItemRepository.save(testBudgetItem1);

        // When
        Optional<BudgetItem> foundBudgetItem = budgetItemRepository.findById(savedBudgetItem.getId());

        // Then
        assertThat(foundBudgetItem).isPresent();
        assertThat(foundBudgetItem.get().getAmount()).isEqualTo(1500.0);
        assertThat(foundBudgetItem.get().getCategory().getName()).isEqualTo("Catering");
    }

    @Test
    @DisplayName("Should find all budget items")
    public void shouldFindAllBudgetItems() {
        // Given
        budgetItemRepository.save(testBudgetItem1);
        budgetItemRepository.save(testBudgetItem2);
        budgetItemRepository.save(testBudgetItem3);

        // When
        List<BudgetItem> allBudgetItems = budgetItemRepository.findAll();

        // Then
        assertThat(allBudgetItems).hasSize(3);
        assertThat(allBudgetItems).extracting(BudgetItem::getAmount)
                .containsExactlyInAnyOrder(1500.0, 800.0, 2000.0);
    }

    @Test
    @DisplayName("Should delete budget item")
    public void shouldDeleteBudgetItem() {
        // Given
        BudgetItem savedBudgetItem = budgetItemRepository.save(testBudgetItem1);

        // When
        budgetItemRepository.delete(savedBudgetItem);

        // Then
        Optional<BudgetItem> deletedBudgetItem = budgetItemRepository.findById(savedBudgetItem.getId());
        assertThat(deletedBudgetItem).isEmpty();
    }

    @Test
    @DisplayName("Should update budget item amount")
    public void shouldUpdateBudgetItemAmount() {
        // Given
        BudgetItem savedBudgetItem = budgetItemRepository.save(testBudgetItem1);
        Double newAmount = 2500.0;

        // When
        savedBudgetItem.setAmount(newAmount);
        BudgetItem updatedBudgetItem = budgetItemRepository.save(savedBudgetItem);

        // Then
        assertThat(updatedBudgetItem.getAmount()).isEqualTo(newAmount);
        assertThat(updatedBudgetItem.getId()).isEqualTo(savedBudgetItem.getId());
    }
}