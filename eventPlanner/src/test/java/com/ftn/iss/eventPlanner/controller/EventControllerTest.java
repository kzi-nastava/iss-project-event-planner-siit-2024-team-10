package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.agendaitem.*;
import com.ftn.iss.eventPlanner.dto.budgetitem.*;
import com.ftn.iss.eventPlanner.dto.event.*;
import com.ftn.iss.eventPlanner.dto.location.CreateLocationDTO;
import com.ftn.iss.eventPlanner.model.Account;
import com.ftn.iss.eventPlanner.model.Role;
import com.ftn.iss.eventPlanner.util.TokenUtils;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static com.ftn.iss.eventPlanner.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class EventControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TokenUtils tokenUtils;

    private String organizerToken;
    private String adminToken;
    private String userToken;

    @BeforeEach
    public void setUp() {
        // EVENT_ORGANIZER token
        Account organizerAccount = new Account();
        organizerAccount.setId(1);
        organizerAccount.setEmail("organizer@mail.com");
        organizerAccount.setRole(Role.EVENT_ORGANIZER);
        organizerToken = tokenUtils.generateToken(organizerAccount);

        // ADMIN token
        Account adminAccount = new Account();
        adminAccount.setId(2);
        adminAccount.setEmail("admin@mail.com");
        adminAccount.setRole(Role.ADMIN);
        adminToken = tokenUtils.generateToken(adminAccount);

        // USER token
        Account userAccount = new Account();
        userAccount.setId(3);
        userAccount.setEmail("auth@mail.com");
        userAccount.setRole(Role.AUTHENTICATED_USER);
        userToken = tokenUtils.generateToken(userAccount);
    }

    private HttpHeaders getHeadersWithAuth(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }

    private HttpHeaders getHeadersWithAuth() {
        return getHeadersWithAuth(organizerToken);
    }

// ==================== createBudgetItem() Tests ====================

    @Test
    @DisplayName("Create Budget Item - Success with valid input")
    public void createBudgetItem_Success() {
        CreateBudgetItemDTO dto = new CreateBudgetItemDTO();
        dto.setAmount(1000);
        dto.setCategoryId(1);

        HttpEntity<CreateBudgetItemDTO> request = new HttpEntity<>(dto, getHeadersWithAuth());

        ResponseEntity<CreatedBudgetItemDTO> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget",
                HttpMethod.POST,
                request,
                CreatedBudgetItemDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1000, response.getBody().getAmount());
    }

    @Test
    @DisplayName("Create Budget Item - Success with zero amount")
    public void createBudgetItem_SuccessWithZeroAmount() {
        CreateBudgetItemDTO dto = new CreateBudgetItemDTO();
        dto.setAmount(0);
        dto.setCategoryId(1);

        HttpEntity<CreateBudgetItemDTO> request = new HttpEntity<>(dto, getHeadersWithAuth());

        ResponseEntity<CreatedBudgetItemDTO> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget",
                HttpMethod.POST,
                request,
                CreatedBudgetItemDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getAmount());
    }

// ==================== Validation Tests ====================

    @Test
    @DisplayName("Create Budget Item - Fails with negative amount")
    public void createBudgetItem_FailsWithNegativeAmount() {
        CreateBudgetItemDTO dto = new CreateBudgetItemDTO();
        dto.setAmount(-100);
        dto.setCategoryId(1);

        HttpEntity<CreateBudgetItemDTO> request = new HttpEntity<>(dto, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget",
                HttpMethod.POST,
                request,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Amount cannot be negative"));
    }

// ==================== Business Logic Tests ====================

    @Test
    @DisplayName("Create Budget Item - Event not found")
    public void createBudgetItem_EventNotFound() {
        CreateBudgetItemDTO dto = new CreateBudgetItemDTO();
        dto.setAmount(500);
        dto.setCategoryId(1);

        HttpEntity<CreateBudgetItemDTO> request = new HttpEntity<>(dto, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + NON_EXISTENT_EVENT_ID + "/budget",
                HttpMethod.POST,
                request,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Event with ID"));
    }

    @Test
    @DisplayName("Create Budget Item - Deleted event")
    public void createBudgetItem_DeletedEvent() {
        CreateBudgetItemDTO dto = new CreateBudgetItemDTO();
        dto.setAmount(500);
        dto.setCategoryId(1);

        HttpEntity<CreateBudgetItemDTO> request = new HttpEntity<>(dto, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + DELETED_EVENT_ID + "/budget",
                HttpMethod.POST,
                request,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("deleted"));
    }

    @Test
    @DisplayName("Create Budget Item - Category not found")
    public void createBudgetItem_CategoryNotFound() {
        CreateBudgetItemDTO dto = new CreateBudgetItemDTO();
        dto.setAmount(500);
        dto.setCategoryId(99999);

        HttpEntity<CreateBudgetItemDTO> request = new HttpEntity<>(dto, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget",
                HttpMethod.POST,
                request,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Offering category with ID"));
    }

    @Test
    @DisplayName("Create Budget Item - Deleted category")
    public void createBudgetItem_DeletedCategory() {
        CreateBudgetItemDTO dto = new CreateBudgetItemDTO();
        dto.setAmount(500);
        dto.setCategoryId(DELETED_CATEGORY_ID);

        HttpEntity<CreateBudgetItemDTO> request = new HttpEntity<>(dto, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget",
                HttpMethod.POST,
                request,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("deleted"));
    }

// ==================== Security Tests ====================

    @Test
    @DisplayName("Create Budget Item - Unauthorized without token")
    public void createBudgetItem_Unauthorized() {
        CreateBudgetItemDTO dto = new CreateBudgetItemDTO();
        dto.setAmount(500);
        dto.setCategoryId(1);

        HttpEntity<CreateBudgetItemDTO> request = new HttpEntity<>(dto);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget",
                HttpMethod.POST,
                request,
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Create Budget Item - Forbidden with USER role")
    public void createBudgetItem_ForbiddenUserRole() {
        CreateBudgetItemDTO dto = new CreateBudgetItemDTO();
        dto.setAmount(500);
        dto.setCategoryId(1);

        HttpEntity<CreateBudgetItemDTO> request = new HttpEntity<>(dto, getHeadersWithAuth(userToken));

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget",
                HttpMethod.POST,
                request,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }


    // ==================== updateBudgetItem() Tests ====================

    @Test
    @DisplayName("Update Budget Item Amount - Success with valid amount")
    public void updateBudgetItemAmount_Success() {
        UpdateBudgetItemDTO dto = new UpdateBudgetItemDTO();
        dto.setAmount(2000);

        HttpEntity<UpdateBudgetItemDTO> request = new HttpEntity<>(dto, getHeadersWithAuth());

        ResponseEntity<UpdatedBudgetItemDTO> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + EXISTING_BUDGET_ITEM_ID,
                HttpMethod.PUT,
                request,
                UpdatedBudgetItemDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2000, response.getBody().getAmount());
    }

    @Test
    @DisplayName("Update Budget Item Amount - Success with zero amount")
    public void updateBudgetItemAmount_SuccessWithZeroAmount() {
        UpdateBudgetItemDTO dto = new UpdateBudgetItemDTO();
        dto.setAmount(0);

        HttpEntity<UpdateBudgetItemDTO> request = new HttpEntity<>(dto, getHeadersWithAuth());

        ResponseEntity<UpdatedBudgetItemDTO> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + EXISTING_BUDGET_ITEM_ID,
                HttpMethod.PUT,
                request,
                UpdatedBudgetItemDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getAmount());
    }

    @Test
    @DisplayName("Update Budget Item Amount - Success with amount equal to used amount")
    public void updateBudgetItemAmount_SuccessWithAmountEqualToUsedAmount() {
        UpdateBudgetItemDTO dto = new UpdateBudgetItemDTO();
        dto.setAmount(1930);

        HttpEntity<UpdateBudgetItemDTO> request = new HttpEntity<>(dto, getHeadersWithAuth());

        ResponseEntity<UpdatedBudgetItemDTO> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + BUDGET_ITEM_WITH_SERVICES_AND_PRODUCTS,
                HttpMethod.PUT,
                request,
                UpdatedBudgetItemDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1930, response.getBody().getAmount());
    }

// ==================== Validation Tests ====================

    @Test
    @DisplayName("Update Budget Item Amount - Amount less than used amount")
    public void updateBudgetItemAmount_AmountLessThanUsedAmount() {
        UpdateBudgetItemDTO dto = new UpdateBudgetItemDTO();
        dto.setAmount(100);

        HttpEntity<UpdateBudgetItemDTO> request = new HttpEntity<>(dto, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + BUDGET_ITEM_WITH_SERVICES_AND_PRODUCTS,
                HttpMethod.PUT,
                request,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("New amount cannot be less than the amount already used"));
    }

// ==================== Business Logic Tests ====================

    @Test
    @DisplayName("Update Budget Item Amount - Budget item not found")
    public void updateBudgetItemAmount_BudgetItemNotFound() {
        UpdateBudgetItemDTO dto = new UpdateBudgetItemDTO();
        dto.setAmount(2000);

        HttpEntity<UpdateBudgetItemDTO> request = new HttpEntity<>(dto, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + NON_EXISTENT_BUDGET_ITEM_ID,
                HttpMethod.PUT,
                request,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Budget item with ID"));
        assertTrue(response.getBody().contains("not found"));
    }

    @Test
    @DisplayName("Update Budget Item Amount - Deleted budget item")
    public void updateBudgetItemAmount_DeletedBudgetItem() {
        UpdateBudgetItemDTO dto = new UpdateBudgetItemDTO();
        dto.setAmount(1000);

        HttpEntity<UpdateBudgetItemDTO> request = new HttpEntity<>(dto, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + DELETED_BUDGET_ITEM_ID,
                HttpMethod.PUT,
                request,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Cannot update deleted budget item"));
    }

// ==================== Security Tests ====================

    @Test
    @DisplayName("Update Budget Item Amount - Unauthorized without token")
    public void updateBudgetItemAmount_Unauthorized() {
        UpdateBudgetItemDTO dto = new UpdateBudgetItemDTO();
        dto.setAmount(2000);

        HttpEntity<UpdateBudgetItemDTO> request = new HttpEntity<>(dto);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + EXISTING_BUDGET_ITEM_ID,
                HttpMethod.PUT,
                request,
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Update Budget Item Amount - Forbidden with USER role")
    public void updateBudgetItemAmount_ForbiddenUserRole() {
        UpdateBudgetItemDTO dto = new UpdateBudgetItemDTO();
        dto.setAmount(2000);

        HttpEntity<UpdateBudgetItemDTO> request = new HttpEntity<>(dto, getHeadersWithAuth(userToken));

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + EXISTING_BUDGET_ITEM_ID,
                HttpMethod.PUT,
                request,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
// ==================== buyOfferingForEvent() Tests ====================

    @Test
    @DisplayName("Buy Offering - Success")
    public void buyOffering_Success() {
        ResponseEntity<Void> response = restTemplate.exchange(
                BASE + "/" + EVENT_WITHOUT_BUDGET_ITEM + "/budget/buy/" + EXISTING_OFFERING_ID,
                HttpMethod.PUT,
                new HttpEntity<>(null, getHeadersWithAuth()),
                Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Buy Offering - Success adds to existing budget item")
    public void buyOffering_SuccessExistingBudgetItem() {
        ResponseEntity<Void> response = restTemplate.exchange(
                BASE + "/" + EVENT_WITH_BUDGET_ITEM + "/budget/buy/" + EXISTING_OFFERING_ID,
                HttpMethod.PUT,
                new HttpEntity<>(null, getHeadersWithAuth()),
                Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

// ==================== Business Logic Tests ====================

    @Test
    @DisplayName("Buy Offering - Event not found")
    public void buyOffering_EventNotFound() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + NON_EXISTENT_EVENT_ID + "/budget/buy/" + EXISTING_OFFERING_ID,
                HttpMethod.PUT,
                new HttpEntity<>(null, getHeadersWithAuth()),
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Event with ID"));
        assertTrue(response.getBody().contains("not found"));
    }

    @Test
    @DisplayName("Buy Offering - Deleted event")
    public void buyOffering_DeletedEvent() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + DELETED_EVENT_ID + "/budget/buy/" + EXISTING_OFFERING_ID,
                HttpMethod.PUT,
                new HttpEntity<>(null, getHeadersWithAuth()),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("is deleted"));
    }

    @Test
    @DisplayName("Buy Offering - Offering not found")
    public void buyOffering_OfferingNotFound() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/buy/" + NON_EXISTENT_OFFERING_ID,
                HttpMethod.PUT,
                new HttpEntity<>(null, getHeadersWithAuth()),
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Offering with ID"));
        assertTrue(response.getBody().contains("not found"));
    }

    @Test
    @DisplayName("Buy Offering - Deleted offering")
    public void buyOffering_DeletedOffering() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/buy/" + DELETED_OFFERING_ID,
                HttpMethod.PUT,
                new HttpEntity<>(null, getHeadersWithAuth()),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("is deleted"));
    }

    @Test
    @DisplayName("Buy Offering - Insufficient budget")
    public void buyOffering_InsufficientBudget() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/buy/" + EXPENSIVE_OFFERING_ID,
                HttpMethod.PUT,
                new HttpEntity<>(null, getHeadersWithAuth()),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Insufficient budget"));
    }

    @Test
    @DisplayName("Buy Offering - Product already purchased")
    public void buyOffering_ProductAlreadyPurchased() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EVENT_WITH_BUDGET_ITEM + "/budget/buy/" + ALREADY_PURCHASED_PRODUCT_ID,
                HttpMethod.PUT,
                new HttpEntity<>(null, getHeadersWithAuth()),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Product already purchased"));
    }

// ==================== Security Tests ====================

    @Test
    @DisplayName("Buy Offering - Unauthorized without token")
    public void buyOffering_Unauthorized() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/buy/" + EXISTING_OFFERING_ID,
                HttpMethod.PUT,
                new HttpEntity<>(null),
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Buy Offering - Forbidden with USER role")
    public void buyOffering_ForbiddenUserRole() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/buy/" + EXISTING_OFFERING_ID,
                HttpMethod.PUT,
                new HttpEntity<>(null, getHeadersWithAuth(userToken)),
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // ==================== deleteBudgetItem() Tests ====================
    @Test
    @DisplayName("Delete Budget Item - Success")
    public void deleteBudgetItem_Success() {
        ResponseEntity<Void> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + BUDGET_ITEM_TO_DELETE,
                HttpMethod.DELETE,
                new HttpEntity<>(null, getHeadersWithAuth()),
                Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Delete Budget Item - Unauthorized")
    public void deleteBudgetItem_Unauthorized() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + BUDGET_ITEM_TO_DELETE,
                HttpMethod.DELETE,
                new HttpEntity<>(null),
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Delete Budget Item - Forbidden with USER role")
    public void deleteBudgetItem_UnauthorizedUserRole() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + BUDGET_ITEM_TO_DELETE,
                HttpMethod.DELETE,
                new HttpEntity<>(null, getHeadersWithAuth(userToken)),
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Delete Budget Item - Budget item not found")
    public void deleteBudgetItem_BudgetItemNotFound() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + NON_EXISTENT_BUDGET_ITEM_ID,
                HttpMethod.DELETE,
                new HttpEntity<>(null, getHeadersWithAuth()),
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Budget item with ID"));
    }

    @Test
    @DisplayName("Delete Budget Item - Event not found")
    public void deleteBudgetItem_EventNotFound() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + NON_EXISTENT_EVENT_ID + "/budget/" + BUDGET_ITEM_TO_DELETE,
                HttpMethod.DELETE,
                new HttpEntity<>(null, getHeadersWithAuth()),
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Event with ID"));
    }

    @Test
    @DisplayName("Delete Budget Item - Cannot delete item with purchases")
    public void deleteBudgetItem_CannotDeleteWithPurchases() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + BUDGET_ITEM_WITH_SERVICES_AND_PRODUCTS,
                HttpMethod.DELETE,
                new HttpEntity<>(null, getHeadersWithAuth()),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Budget item cannot be deleted"));
    }

    @Test
    @DisplayName("Delete Budget Item - Already deleted item")
    public void deleteBudgetItem_AlreadyDeleted() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + DELETED_BUDGET_ITEM_ID,
                HttpMethod.DELETE,
                new HttpEntity<>(null, getHeadersWithAuth()),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("is deleted"));
    }


    @Test
    @DisplayName("Get Total Budget - Success for event with budget items")
    public void getTotalBudget_Success() {
        HttpEntity<Void> request = new HttpEntity<>(getHeadersWithAuth());

        ResponseEntity<Double> response = restTemplate.exchange(
                BASE + "/" + EVENT_WITH_BUDGET_ITEM + "/budget/total",
                HttpMethod.GET,
                request,
                Double.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1930.0, response.getBody());
    }

    @Test
    @DisplayName("Get Total Budget - Event with no budget items returns zero")
    public void getTotalBudget_Zero() {
        HttpEntity<Void> request = new HttpEntity<>(getHeadersWithAuth());

        ResponseEntity<Double> response = restTemplate.exchange(
                BASE + "/" + EVENT_WITHOUT_BUDGET_ITEM + "/budget/total",
                HttpMethod.GET,
                request,
                Double.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0.0, response.getBody());
    }

    @Test
    @DisplayName("Get Total Budget - Non-existent event returns 404")
    public void getTotalBudget_NonExistentEvent() {
        HttpEntity<Void> request = new HttpEntity<>(getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + NON_EXISTENT_EVENT_ID + "/budget/total",
                HttpMethod.GET,
                request,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Get Budget Items - Success for existing event")
    public void getBudgetItems_Success() {
        HttpEntity<Void> request = new HttpEntity<>(getHeadersWithAuth());

        ResponseEntity<GetBudgetItemDTO[]> response = restTemplate.exchange(
                BASE + "/budget/" + EXISTING_EVENT_ID,
                HttpMethod.GET,
                request,
                GetBudgetItemDTO[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().length);
    }

    @Test
    @DisplayName("Get Budget Items - Event has no budget items")
    public void getBudgetItems_EmptyList() {
        HttpEntity<Void> request = new HttpEntity<>(getHeadersWithAuth());

        ResponseEntity<GetBudgetItemDTO[]> response = restTemplate.exchange(
                BASE + "/budget/" + EVENT_WITHOUT_BUDGET,
                HttpMethod.GET,
                request,
                GetBudgetItemDTO[].class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().length);
    }

    @Test
    @DisplayName("Get Budget Items - Non-existent event returns 404 Not Found")
    public void getBudgetItems_NonExistentEvent() {
        HttpEntity<Void> request = new HttpEntity<>(getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/budget/" + NON_EXISTENT_EVENT_ID,
                HttpMethod.GET,
                request,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Get Budget Items - Forbidden role")
    public void getBudgetItems_UnauthorizedRole() {
        HttpEntity<Void> request = new HttpEntity<>(getHeadersWithAuth(userToken)); // Not an organizer

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/budget/" + EVENT_WITH_BUDGET_ITEM,
                HttpMethod.GET,
                request,
                String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // Helper method to create a valid CreateEventDTO with existing test data
    private CreateEventDTO createValidEventDTO() {
        CreateEventDTO createEventDTO = new CreateEventDTO();
        createEventDTO.setEventTypeId(1);
        createEventDTO.setOrganizerId(2);
        createEventDTO.setName("Test Event");
        createEventDTO.setDescription("Test description");
        createEventDTO.setMaxParticipants(100);
        createEventDTO.setOpen(true);
        createEventDTO.setDate(LocalDate.now().plusDays(5));

        CreateLocationDTO locationDTO = new CreateLocationDTO();
        locationDTO.setStreet("123 Street");
        locationDTO.setHouseNumber("456");
        locationDTO.setCity("Testville");
        locationDTO.setCountry("Testland");
        createEventDTO.setLocation(locationDTO);

        return createEventDTO;
    }

    // Event Creation Tests
    @Test
    @DisplayName("Should create event successfully with valid data and EVENT_ORGANIZER role")
    void createEvent_WithValidDataAndOrganizerRole_ReturnsCreated() {
        CreateEventDTO createEventDTO = createValidEventDTO();

        HttpEntity<CreateEventDTO> request = new HttpEntity<>(createEventDTO, getHeadersWithAuth());

        ResponseEntity<CreatedEventDTO> response = restTemplate.exchange(
                BASE,
                HttpMethod.POST,
                request,
                CreatedEventDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Event", response.getBody().getName());
        assertEquals("Test description", response.getBody().getDescription());
        assertEquals(100, response.getBody().getMaxParticipants());
        assertTrue(response.getBody().isOpen());
        assertFalse(response.getBody().isDeleted());
    }

    @Test
    @DisplayName("Should return 401 when creating event without authentication")
    void createEvent_WithoutAuthentication_ReturnsUnauthorized() {
        CreateEventDTO createEventDTO = createValidEventDTO();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateEventDTO> request = new HttpEntity<>(createEventDTO, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE,
                HttpMethod.POST,
                request,
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 403 when creating event with USER role")
    void createEvent_WithUserRole_ReturnsForbidden() {
        CreateEventDTO createEventDTO = createValidEventDTO();

        HttpEntity<CreateEventDTO> request = new HttpEntity<>(createEventDTO, getHeadersWithAuth(userToken));

        ResponseEntity<String> response = restTemplate.exchange(
                BASE,
                HttpMethod.POST,
                request,
                String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 400 when creating event with date in the past")
    void createEvent_WithPastDate_ReturnsBadRequest() {
        CreateEventDTO createEventDTO = createValidEventDTO();
        createEventDTO.setDate(LocalDate.now().minusDays(1)); // Past date

        HttpEntity<CreateEventDTO> request = new HttpEntity<>(createEventDTO, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE,
                HttpMethod.POST,
                request,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Event date must be in the future"));
    }

    @Test
    @DisplayName("Should return 400 when creating event with non-existent event type")
    void createEvent_WithNonExistentEventType_ReturnsBadRequest() {
        CreateEventDTO createEventDTO = createValidEventDTO();
        createEventDTO.setEventTypeId(999);

        HttpEntity<CreateEventDTO> request = new HttpEntity<>(createEventDTO, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE,
                HttpMethod.POST,
                request,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Event Type with ID 999 not found"));
    }

    @Test
    @DisplayName("Should return 400 when creating event with non-existent organizer")
    void createEvent_WithNonExistentOrganizer_ReturnsBadRequest() {
        CreateEventDTO createEventDTO = createValidEventDTO();
        createEventDTO.setOrganizerId(NON_EXISTENT_ORGANIZER_ID);

        HttpEntity<CreateEventDTO> request = new HttpEntity<>(createEventDTO, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE,
                HttpMethod.POST,
                request,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Organizer with ID " + NON_EXISTENT_ORGANIZER_ID + " not found"));
    }


    //helper method to create a valid UpdateEventDTO with existing test data
    private static UpdateEventDTO createValidUpdateEventDTO() {
        UpdateEventDTO updateEventDTO = new UpdateEventDTO();
        updateEventDTO.setName("Updated Test Event");
        updateEventDTO.setDescription("Updated Test description");
        updateEventDTO.setMaxParticipants(150);
        updateEventDTO.setOpen(false);
        updateEventDTO.setDate(LocalDate.now().plusDays(10));
        updateEventDTO.setEventTypeId(2);

        CreateLocationDTO locationDTO = new CreateLocationDTO();
        locationDTO.setCity("Novi Sad");
        locationDTO.setCountry("Serbia");
        locationDTO.setStreet("Bulevar Oslobodjenja");
        locationDTO.setHouseNumber("125");
        updateEventDTO.setLocation(locationDTO);
        return updateEventDTO;
    }

    // Event Update Tests
    @Test
    @DisplayName("Should update event successfully with valid data")
    void updateEvent_WithValidData_ReturnsUpdated() {
        UpdateEventDTO updateEventDTO = createValidUpdateEventDTO();

        HttpEntity<UpdateEventDTO> request = new HttpEntity<>(updateEventDTO, getHeadersWithAuth());

        ResponseEntity<UpdatedEventDTO> response = restTemplate.exchange(
                BASE + "/"+ UPDATABLE_EVENT_ID,
                HttpMethod.PUT,
                request,
                UpdatedEventDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Test Event", response.getBody().getName());
        assertEquals("Updated Test description", response.getBody().getDescription());
        assertEquals(150, response.getBody().getMaxParticipants());
        assertFalse(response.getBody().isOpen());
    }

    @Test
    @DisplayName("Should return 401 when updating event without authentication")
    void updateEvent_WithoutAuthentication_ReturnsUnauthorized() {
        UpdateEventDTO updateEventDTO = createValidUpdateEventDTO();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UpdateEventDTO> request = new HttpEntity<>(updateEventDTO, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/"+ UPDATABLE_EVENT_ID,
                HttpMethod.PUT,
                request,
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 403 when updating event with USER role")
    void updateEvent_WithUserRole_ReturnsForbidden() {
        UpdateEventDTO updateEventDTO = createValidUpdateEventDTO();

        HttpEntity<UpdateEventDTO> request = new HttpEntity<>(updateEventDTO, getHeadersWithAuth(userToken));

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/"+ UPDATABLE_EVENT_ID,
                HttpMethod.PUT,
                request,
                String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 400 when updating event with past date")
    void updateEvent_WithPastDate_ReturnsBadRequest() {
        UpdateEventDTO updateEventDTO = createValidUpdateEventDTO();

        updateEventDTO.setDate(LocalDate.now().minusDays(1));

        HttpEntity<UpdateEventDTO> request = new HttpEntity<>(updateEventDTO, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/"+ UPDATABLE_EVENT_ID,
                HttpMethod.PUT,
                request,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Event date must be in the future"));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent event")
    void updateEvent_WithNonExistentEvent_ReturnsNotFound() {
        UpdateEventDTO updateEventDTO = createValidUpdateEventDTO();

        HttpEntity<UpdateEventDTO> request = new HttpEntity<>(updateEventDTO, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + NON_EXISTENT_EVENT_ID,
                HttpMethod.PUT,
                request,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Event with ID " + NON_EXISTENT_EVENT_ID + " not found"));
    }

    @Test
    @DisplayName("Should return 400 when updating event with non-existent event type")
    void updateEvent_WithNonExistentEventType_ReturnsBadRequest() {
        UpdateEventDTO updateEventDTO = createValidUpdateEventDTO();
        updateEventDTO.setEventTypeId(NON_EXISTENT_EVENT_TYPE_ID);

        HttpEntity<UpdateEventDTO> request = new HttpEntity<>(updateEventDTO, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE+"/3",
                HttpMethod.PUT,
                request,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Event Type with ID " + NON_EXISTENT_EVENT_TYPE_ID + " not found"));
    }

    // Event Delete Tests
    @Test
    @DisplayName("Should delete event successfully")
    void deleteEvent_WithValidId_ReturnsNoContent() {
        HttpEntity<Void> request = new HttpEntity<>(getHeadersWithAuth());

        ResponseEntity<Void> response = restTemplate.exchange(
                BASE + "/"+ DELETABLE_EVENT_ID,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent event")
    void deleteEvent_NonExistent_ReturnsNotFound() {
        HttpEntity<Void> request = new HttpEntity<>(getHeadersWithAuth());

        ResponseEntity<Void> response = restTemplate.exchange(
                BASE + "/" + NON_EXISTENT_EVENT_ID,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 401 when deleting event without authentication")
    void deleteEvent_WithoutAuthentication_ReturnsUnauthorized() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                BASE + "/"+ DELETABLE_EVENT_ID,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 403 when deleting event with USER role")
    void deleteEvent_WithUserRole_ReturnsForbidden() {
        HttpEntity<Void> request = new HttpEntity<>(getHeadersWithAuth(userToken));

        ResponseEntity<Void> response = restTemplate.exchange(
                BASE + "/" + DELETABLE_EVENT_ID,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    //helper method to create a valid CreateAgendaItemDTO with existing test data
    private static @NotNull CreateAgendaItemDTO createValidCreateAgendaItemDTO() {
        CreateAgendaItemDTO createAgendaItemDTO = new CreateAgendaItemDTO();
        createAgendaItemDTO.setName("Opening Ceremony");
        createAgendaItemDTO.setDescription("Welcome speech and overview of the event.");
        createAgendaItemDTO.setLocation("Main Hall");
        createAgendaItemDTO.setStartTime(LocalTime.of(9, 0));
        createAgendaItemDTO.setEndTime(LocalTime.of(10, 0));
        return createAgendaItemDTO;
    }

    // Agenda Item Tests
    @Test
    @DisplayName("Should create agenda item successfully")
    void createAgendaItem_WithValidData_ReturnsCreated() {
        CreateAgendaItemDTO createAgendaItemDTO = createValidCreateAgendaItemDTO();

        HttpEntity<CreateAgendaItemDTO> request = new HttpEntity<>(createAgendaItemDTO, getHeadersWithAuth());

        ResponseEntity<CreatedAgendaItemDTO> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/agenda",
                HttpMethod.POST,
                request,
                CreatedAgendaItemDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Opening Ceremony", response.getBody().getName());
        assertEquals("Welcome speech and overview of the event.", response.getBody().getDescription());
        assertEquals("Main Hall", response.getBody().getLocation());
    }

    @Test
    @DisplayName("Should return 401 when creating agenda item without authentication")
    void createAgendaItem_WithoutAuth_ReturnsUnauthorized() {
        CreateAgendaItemDTO createAgendaItemDTO = createValidCreateAgendaItemDTO();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateAgendaItemDTO> request = new HttpEntity<>(createAgendaItemDTO, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/agenda",
                HttpMethod.POST,
                request,
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 403 when creating agenda item with USER role")
    void createAgendaItem_WithUserRole_ReturnsForbidden() {
        CreateAgendaItemDTO createAgendaItemDTO = createValidCreateAgendaItemDTO();

        HttpEntity<CreateAgendaItemDTO> request = new HttpEntity<>(createAgendaItemDTO, getHeadersWithAuth(userToken));

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/agenda",
                HttpMethod.POST,
                request,
                String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 400 when creating agenda item with start time after end time")
    void createAgendaItem_WithStartTimeAfterEndTime_ReturnsBadRequest() {
        CreateAgendaItemDTO createAgendaItemDTO = createValidCreateAgendaItemDTO();
        createAgendaItemDTO.setStartTime(LocalTime.of(12, 0));
        createAgendaItemDTO.setEndTime(LocalTime.of(10, 0));

        HttpEntity<CreateAgendaItemDTO> request = new HttpEntity<>(createAgendaItemDTO, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/agenda",
                HttpMethod.POST,
                request,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Start time must be before end time"));
    }

    @Test
    @DisplayName("Should return 404 when creating agenda item for non-existent event")
    void createAgendaItem_WithNonExistentEvent_ReturnsNotFound() {
        CreateAgendaItemDTO createAgendaItemDTO = createValidCreateAgendaItemDTO();

        HttpEntity<CreateAgendaItemDTO> request = new HttpEntity<>(createAgendaItemDTO, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + NON_EXISTENT_EVENT_ID + "/agenda",
                HttpMethod.POST,
                request,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Event with ID " + NON_EXISTENT_EVENT_ID + " not found"));
    }

    private static UpdateAgendaItemDTO createValidUpdateAgendaItemDTO() {
        UpdateAgendaItemDTO updateAgendaItemDTO = new UpdateAgendaItemDTO();
        updateAgendaItemDTO.setName("Keynote Speech");
        updateAgendaItemDTO.setDescription("An inspiring opening speech by the keynote speaker.");
        updateAgendaItemDTO.setLocation("Auditorium A");
        updateAgendaItemDTO.setStartTime(LocalTime.of(10, 0));
        updateAgendaItemDTO.setEndTime(LocalTime.of(11, 0));
        return updateAgendaItemDTO;
    }

    @Test
    @DisplayName("Should update agenda item successfully")
    @Transactional
    void updateAgendaItem_WithValidData_ReturnsUpdated() {
        UpdateAgendaItemDTO updateAgendaItemDTO = createValidUpdateAgendaItemDTO();

        HttpEntity<UpdateAgendaItemDTO> request = new HttpEntity<>(updateAgendaItemDTO, getHeadersWithAuth());

        ResponseEntity<UpdatedAgendaItemDTO> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/agenda/" + EXISTING_AGENDA_ITEM_ID,
                HttpMethod.PUT,
                request,
                UpdatedAgendaItemDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Keynote Speech", response.getBody().getName());
        assertEquals("An inspiring opening speech by the keynote speaker.", response.getBody().getDescription());
        assertEquals("Auditorium A", response.getBody().getLocation());
    }

    @Test
    @DisplayName("Should return 400 when updating agenda item with start time after end time")
    void updateAgendaItem_WithStartTimeAfterEndTime_ReturnsBadRequest() {
        UpdateAgendaItemDTO updateAgendaItemDTO = createValidUpdateAgendaItemDTO();
        updateAgendaItemDTO.setStartTime(LocalTime.of(12, 0));
        updateAgendaItemDTO.setEndTime(LocalTime.of(11, 0));

        HttpEntity<UpdateAgendaItemDTO> request = new HttpEntity<>(updateAgendaItemDTO, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/agenda/" + EXISTING_AGENDA_ITEM_ID,
                HttpMethod.PUT,
                request,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Start time must be before end time"));
    }

    @Test
    @DisplayName("Should return 401 when updating agenda item without authentication")
    void updateAgendaItem_WithoutAuth_ReturnsUnauthorized() {
        UpdateAgendaItemDTO updateAgendaItemDTO = createValidUpdateAgendaItemDTO();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UpdateAgendaItemDTO> request = new HttpEntity<>(updateAgendaItemDTO, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/agenda/" + EXISTING_AGENDA_ITEM_ID,
                HttpMethod.PUT,
                request,
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 403 when updating agenda item with USER role")
    void updateAgendaItem_WithUserRole_ReturnsForbidden() {
        UpdateAgendaItemDTO updateAgendaItemDTO = createValidUpdateAgendaItemDTO();

        HttpEntity<UpdateAgendaItemDTO> request = new HttpEntity<>(updateAgendaItemDTO, getHeadersWithAuth(userToken));

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/agenda/" + EXISTING_AGENDA_ITEM_ID,
                HttpMethod.PUT,
                request,
                String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 when updating agenda for nonexistent event")
    void updateAgendaItem_NonExistentEvent_ReturnsNotFound() {
        UpdateAgendaItemDTO updateAgendaItemDTO = createValidUpdateAgendaItemDTO();

        HttpEntity<UpdateAgendaItemDTO> request = new HttpEntity<>(updateAgendaItemDTO, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + NON_EXISTENT_EVENT_ID + "/agenda/" + EXISTING_AGENDA_ITEM_ID,
                HttpMethod.PUT,
                request,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 when updating nonexistent agenda item")
    void updateAgendaItem_NonExistentAgendaItem_ReturnsNotFound() {
        UpdateAgendaItemDTO updateAgendaItemDTO = createValidUpdateAgendaItemDTO();

        HttpEntity<UpdateAgendaItemDTO> request = new HttpEntity<>(updateAgendaItemDTO, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/agenda/" + NON_EXISTENT_AGENDA_ITEM_ID,
                HttpMethod.PUT,
                request,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }



    @Test
    @DisplayName("Should delete agenda item successfully")
    void deleteAgendaItem_WithValidData_ReturnsNoContent() {
        HttpEntity<Void> request = new HttpEntity<>(getHeadersWithAuth());

        ResponseEntity<Void> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/agenda/"+ EXISTING_AGENDA_ITEM_ID,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 when deleting agenda item from non-existent event")
    void deleteAgendaItem_WithNonExistentEvent_ReturnsNotFound() {
        HttpEntity<Void> request = new HttpEntity<>(getHeadersWithAuth());

        ResponseEntity<Void> response = restTemplate.exchange(
                BASE + "/" + NON_EXISTENT_EVENT_ID + "/agenda/" + EXISTING_AGENDA_ITEM_ID,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent agenda item")
    void deleteAgendaItem_WithNonExistentAgenda_ReturnsNotFound() {
        HttpEntity<Void> request = new HttpEntity<>(getHeadersWithAuth());

        ResponseEntity<Void> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/agenda/"+ NON_EXISTENT_AGENDA_ITEM_ID,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 401 when deleting agenda item without authentication")
    void deleteAgendaItem_WithoutAuth_ReturnsUnauthorized() {
        CreateAgendaItemDTO createAgendaItemDTO = createValidCreateAgendaItemDTO();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateAgendaItemDTO> request = new HttpEntity<>(createAgendaItemDTO, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/agenda/"+ EXISTING_AGENDA_ITEM_ID,
                HttpMethod.DELETE,
                request,
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 403 when creating agenda item with USER role")
    void deleteAgendaItem_WithUserRole_ReturnsForbidden() {
        CreateAgendaItemDTO createAgendaItemDTO = createValidCreateAgendaItemDTO();

        HttpEntity<CreateAgendaItemDTO> request = new HttpEntity<>(createAgendaItemDTO, getHeadersWithAuth(userToken));

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/agenda/"+ EXISTING_AGENDA_ITEM_ID,
                HttpMethod.DELETE,
                request,
                String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get event agenda successfully")
    void getEventAgenda_WithValidEventId_ReturnsAgenda() {
        HttpEntity<Void> request = new HttpEntity<>(getHeadersWithAuth());

        ResponseEntity<List<GetAgendaItemDTO>> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/agenda",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<GetAgendaItemDTO>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should return 404 when getting agenda for non-existent event")
    void getEventAgenda_WithNonExistentEvent_ReturnsNotFound() {
        HttpEntity<Void> request = new HttpEntity<>(getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + NON_EXISTENT_EVENT_ID + "/agenda",
                HttpMethod.GET,
                request,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get event successfully")
    void getEvent_WithValidEventId_ReturnsEvent() {
        HttpEntity<Void> request = new HttpEntity<>(getHeadersWithAuth());

        ResponseEntity<GetEventDTO> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID,
                HttpMethod.GET,
                request,
                GetEventDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should return 404 when getting non-existent event")
    void getEvent_WithNonExistentEventId_ReturnsNotFound() {
        HttpEntity<Void> request = new HttpEntity<>(getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + NON_EXISTENT_EVENT_ID,
                HttpMethod.GET,
                request,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
