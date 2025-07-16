package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.budgetitem.CreateBudgetItemDTO;
import com.ftn.iss.eventPlanner.dto.budgetitem.CreatedBudgetItemDTO;
import com.ftn.iss.eventPlanner.dto.budgetitem.GetBudgetItemDTO;
import com.ftn.iss.eventPlanner.dto.budgetitem.UpdatedBudgetItemDTO;
import com.ftn.iss.eventPlanner.model.Account;
import com.ftn.iss.eventPlanner.model.Role;
import com.ftn.iss.eventPlanner.util.TokenUtils;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class EventControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TokenUtils tokenUtils;

    private String organizerToken;
    private String adminToken;
    private String userToken;

    private static final String BASE = "/api/events";
    private static final int EXISTING_EVENT_ID = 1;
    private static final int NON_EXISTENT_EVENT_ID = 999;
    private static final int EXISTING_BUDGET_ITEM_ID = 1;
    private static final int NON_EXISTENT_BUDGET_ITEM_ID = 999;
    private static final int EXISTING_OFFERING_ID = 1;
    private static final int NON_EXISTENT_OFFERING_ID = 999;
    private static final int DELETED_EVENT_ID = 10;
    private static final int DELETED_BUDGET_ITEM_ID = 2;
    private static final int DELETED_CATEGORY_ID = 3;
    private static final int EVENT_WITH_BUDGET_ITEM = 3;
    private static final int BUDGET_ITEM_TO_DELETE = 3;

    private static final int EVENT_WITHOUT_BUDGET_ITEM = 2;
    private static final int BUDGET_ITEM_WITH_SERVICES_AND_PRODUCTS = 4; // Budget item with services and products

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
        userAccount.setEmail("user@mail.com");
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

        assertEquals(HttpStatus.OK, response.getStatusCode());
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

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getAmount());
    }

    @Test
    @DisplayName("Create Budget Item - Success with maximum integer amount")
    public void createBudgetItem_SuccessWithMaxAmount() {
        CreateBudgetItemDTO dto = new CreateBudgetItemDTO();
        dto.setAmount(Integer.MAX_VALUE);
        dto.setCategoryId(1);

        HttpEntity<CreateBudgetItemDTO> request = new HttpEntity<>(dto, getHeadersWithAuth());

        ResponseEntity<CreatedBudgetItemDTO> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget",
                HttpMethod.POST,
                request,
                CreatedBudgetItemDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Integer.MAX_VALUE, response.getBody().getAmount());
    }

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
    @DisplayName("Create Budget Item - Unauthorized with USER role")
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

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Create Budget Item - Unauthorized with ADMIN role")
    public void createBudgetItem_SuccessWithAdminRole() {
        CreateBudgetItemDTO dto = new CreateBudgetItemDTO();
        dto.setAmount(1000);
        dto.setCategoryId(1);

        HttpEntity<CreateBudgetItemDTO> request = new HttpEntity<>(dto, getHeadersWithAuth(adminToken));

        ResponseEntity<CreatedBudgetItemDTO> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget",
                HttpMethod.POST,
                request,
                CreatedBudgetItemDTO.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Create Budget Item - Invalid token")
    public void createBudgetItem_InvalidToken() {
        CreateBudgetItemDTO dto = new CreateBudgetItemDTO();
        dto.setAmount(500);
        dto.setCategoryId(1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("invalid-token");

        HttpEntity<CreateBudgetItemDTO> request = new HttpEntity<>(dto, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget",
                HttpMethod.POST,
                request,
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

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

        assertTrue(response.getStatusCode().is4xxClientError());
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

        assertTrue(response.getStatusCode().is4xxClientError());
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

        assertTrue(response.getStatusCode().is4xxClientError());
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

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Create Budget Item - Negative amount")
    public void createBudgetItem_NegativeAmount() {
        CreateBudgetItemDTO dto = new CreateBudgetItemDTO();
        dto.setAmount(-100);
        dto.setCategoryId(1);

        HttpEntity<CreateBudgetItemDTO> request = new HttpEntity<>(dto, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget",
                HttpMethod.POST,
                request,
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Create Budget Item - Missing categoryId")
    public void createBudgetItem_MissingCategoryId() {
        CreateBudgetItemDTO dto = new CreateBudgetItemDTO();
        dto.setAmount(500);

        HttpEntity<CreateBudgetItemDTO> request = new HttpEntity<>(dto, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget",
                HttpMethod.POST,
                request,
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Create Budget Item - Empty request body")
    public void createBudgetItem_EmptyRequestBody() {
        HttpEntity<String> request = new HttpEntity<>("{}", getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget",
                HttpMethod.POST,
                request,
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Create Budget Item - Invalid JSON")
    public void createBudgetItem_InvalidJSON() {
        HttpEntity<String> request = new HttpEntity<>("invalid-json", getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget",
                HttpMethod.POST,
                request,
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Create Budget Item - Invalid eventId format")
    public void createBudgetItem_InvalidEventIdFormat() {
        CreateBudgetItemDTO dto = new CreateBudgetItemDTO();
        dto.setAmount(500);
        dto.setCategoryId(1);

        HttpEntity<CreateBudgetItemDTO> request = new HttpEntity<>(dto, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/invalid-id/budget",
                HttpMethod.POST,
                request,
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Create Budget Item - Event with existing budget items")
    public void createBudgetItem_EventWithExistingBudgetItems() {
        // First, create a budget item
        CreateBudgetItemDTO dto1 = new CreateBudgetItemDTO();
        dto1.setAmount(500);
        dto1.setCategoryId(1);

        HttpEntity<CreateBudgetItemDTO> request1 = new HttpEntity<>(dto1, getHeadersWithAuth());
        restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget",
                HttpMethod.POST,
                request1,
                CreatedBudgetItemDTO.class);

        // Then create another budget item for the same event
        CreateBudgetItemDTO dto2 = new CreateBudgetItemDTO();
        dto2.setAmount(750);
        dto2.setCategoryId(2);

        HttpEntity<CreateBudgetItemDTO> request2 = new HttpEntity<>(dto2, getHeadersWithAuth());

        ResponseEntity<CreatedBudgetItemDTO> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget",
                HttpMethod.POST,
                request2,
                CreatedBudgetItemDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(750, response.getBody().getAmount());
    }

    // ==================== updateBudgetItem() Tests ====================
    @Test
    @DisplayName("Update Budget Item Amount - Success with zero amount")
    public void updateBudgetItemAmount_SuccessWithZeroAmount() {
        int newAmount = 0;

        HttpEntity<Integer> request = new HttpEntity<>(newAmount, getHeadersWithAuth());

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
    @DisplayName("Update Budget Item Amount - Success with maximum integer amount")
    public void updateBudgetItemAmount_SuccessWithMaxAmount() {
        int newAmount = Integer.MAX_VALUE;

        HttpEntity<Integer> request = new HttpEntity<>(newAmount, getHeadersWithAuth());

        ResponseEntity<UpdatedBudgetItemDTO> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + EXISTING_BUDGET_ITEM_ID,
                HttpMethod.PUT,
                request,
                UpdatedBudgetItemDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Integer.MAX_VALUE, response.getBody().getAmount());
    }

    @Test
    @DisplayName("Update Budget Item Amount - Success with amount equal to used amount")
    public void updateBudgetItemAmount_SuccessWithAmountEqualToUsedAmount() {
        int newAmount = 1930;

        HttpEntity<Integer> request = new HttpEntity<>(newAmount, getHeadersWithAuth());

        ResponseEntity<UpdatedBudgetItemDTO> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + BUDGET_ITEM_WITH_SERVICES_AND_PRODUCTS,
                HttpMethod.PUT,
                request,
                UpdatedBudgetItemDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1930, response.getBody().getAmount());
    }

    @Test
    @DisplayName("Update Budget Item Amount - Success with amount greater than used amount")
    public void updateBudgetItemAmount_SuccessWithAmountGreaterThanUsedAmount() {
        int newAmount = 2222;

        HttpEntity<Integer> request = new HttpEntity<>(newAmount, getHeadersWithAuth());

        ResponseEntity<UpdatedBudgetItemDTO> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + BUDGET_ITEM_WITH_SERVICES_AND_PRODUCTS,
                HttpMethod.PUT,
                request,
                UpdatedBudgetItemDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2222, response.getBody().getAmount());
    }

    @Test
    @DisplayName("Update Budget Item Amount - Unauthorized without token")
    public void updateBudgetItemAmount_Unauthorized() {
        int newAmount = 2000;

        HttpEntity<Integer> request = new HttpEntity<>(newAmount);

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
        int newAmount = 2000;

        HttpEntity<Integer> request = new HttpEntity<>(newAmount, getHeadersWithAuth(userToken));

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + EXISTING_BUDGET_ITEM_ID,
                HttpMethod.PUT,
                request,
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Update Budget Item Amount - Unauthorized with ADMIN role")
    public void updateBudgetItemAmount_SuccessWithAdminRole() {
        int newAmount = 2000;

        HttpEntity<Integer> request = new HttpEntity<>(newAmount, getHeadersWithAuth(adminToken));

        ResponseEntity<UpdatedBudgetItemDTO> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + EXISTING_BUDGET_ITEM_ID,
                HttpMethod.PUT,
                request,
                UpdatedBudgetItemDTO.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Update Budget Item Amount - Invalid token")
    public void updateBudgetItemAmount_InvalidToken() {
        int newAmount = 2000;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("invalid-token");

        HttpEntity<Integer> request = new HttpEntity<>(newAmount, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + EXISTING_BUDGET_ITEM_ID,
                HttpMethod.PUT,
                request,
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Update Budget Item Amount - Budget item not found")
    public void updateBudgetItemAmount_BudgetItemNotFound() {
        int newAmount = 2000;

        HttpEntity<Integer> request = new HttpEntity<>(newAmount, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + NON_EXISTENT_BUDGET_ITEM_ID,
                HttpMethod.PUT,
                request,
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
        assertTrue(response.getBody().contains("Budget item with ID"));
        assertTrue(response.getBody().contains("not found"));
    }

    @Test
    @DisplayName("Update Budget Item Amount - Negative amount")
    public void updateBudgetItemAmount_NegativeAmount() {
        int newAmount = -500;

        HttpEntity<Integer> request = new HttpEntity<>(newAmount, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + EXISTING_BUDGET_ITEM_ID,
                HttpMethod.PUT,
                request,
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Update Budget Item Amount - Amount less than used amount")
    public void updateBudgetItemAmount_AmountLessThanUsedAmount() {
        int newAmount = 100;

        HttpEntity<Integer> request = new HttpEntity<>(newAmount, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + BUDGET_ITEM_WITH_SERVICES_AND_PRODUCTS,
                HttpMethod.PUT,
                request,
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
        assertTrue(response.getBody().contains("New amount cannot be less than the amount already used"));
    }

    @Test
    @DisplayName("Update Budget Item Amount - Invalid eventId format")
    public void updateBudgetItemAmount_InvalidEventIdFormat() {
        int newAmount = 2000;

        HttpEntity<Integer> request = new HttpEntity<>(newAmount, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/invalid-id/budget/" + EXISTING_BUDGET_ITEM_ID,
                HttpMethod.PUT,
                request,
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Update Budget Item Amount - Invalid budgetItemId format")
    public void updateBudgetItemAmount_InvalidBudgetItemIdFormat() {
        int newAmount = 2000;

        HttpEntity<Integer> request = new HttpEntity<>(newAmount, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/invalid-id",
                HttpMethod.PUT,
                request,
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Update Budget Item Amount - Zero budgetItemId")
    public void updateBudgetItemAmount_ZeroBudgetItemId() {
        int newAmount = 2000;

        HttpEntity<Integer> request = new HttpEntity<>(newAmount, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/0",
                HttpMethod.PUT,
                request,
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Update Budget Item Amount - Negative budgetItemId")
    public void updateBudgetItemAmount_NegativeBudgetItemId() {
        int newAmount = 2000;

        HttpEntity<Integer> request = new HttpEntity<>(newAmount, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/-1",
                HttpMethod.PUT,
                request,
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Update Budget Item Amount - Empty request body")
    public void updateBudgetItemAmount_EmptyRequestBody() {
        HttpEntity<String> request = new HttpEntity<>("", getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + EXISTING_BUDGET_ITEM_ID,
                HttpMethod.PUT,
                request,
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Update Budget Item Amount - Invalid JSON")
    public void updateBudgetItemAmount_InvalidJSON() {
        HttpEntity<String> request = new HttpEntity<>("invalid-json", getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + EXISTING_BUDGET_ITEM_ID,
                HttpMethod.PUT,
                request,
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Update Budget Item Amount - String instead of integer")
    public void updateBudgetItemAmount_StringInsteadOfInteger() {
        HttpEntity<String> request = new HttpEntity<>("\"not-a-number\"", getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + EXISTING_BUDGET_ITEM_ID,
                HttpMethod.PUT,
                request,
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Update Budget Item Amount - Null amount")
    public void updateBudgetItemAmount_NullAmount() {
        HttpEntity<Integer> request = new HttpEntity<>(null, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + EXISTING_BUDGET_ITEM_ID,
                HttpMethod.PUT,
                request,
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }


    @Test
    @DisplayName("Update Budget Item Amount - Very large amount")
    public void updateBudgetItemAmount_VeryLargeAmount() {
        int newAmount = Integer.MAX_VALUE - 1;

        HttpEntity<Integer> request = new HttpEntity<>(newAmount, getHeadersWithAuth());

        ResponseEntity<UpdatedBudgetItemDTO> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + EXISTING_BUDGET_ITEM_ID,
                HttpMethod.PUT,
                request,
                UpdatedBudgetItemDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Integer.MAX_VALUE - 1, response.getBody().getAmount());
    }


    @Test
    @DisplayName("Update Budget Item Amount - Calculation with services and products")
    public void updateBudgetItemAmount_CalculationWithServicesAndProducts() {
        // This test requires a budget item with known services and products
        // Assuming BUDGET_ITEM_WITH_SERVICES_AND_PRODUCTS has:
        // - Service: price=1200, discount=10% -> effective price = 1080
        // - Service: price=500, discount=0% -> effective price = 500
        // - Product: price=700, discount=50% -> effective price = 350
        // - Total used amount = 1930

        int newAmount = 1940; // Just above the used amount

        HttpEntity<Integer> request = new HttpEntity<>(newAmount, getHeadersWithAuth());

        ResponseEntity<UpdatedBudgetItemDTO> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + BUDGET_ITEM_WITH_SERVICES_AND_PRODUCTS,
                HttpMethod.PUT,
                request,
                UpdatedBudgetItemDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1940, response.getBody().getAmount());
    }

    @Test
    @DisplayName("Update Budget Item Amount - Calculation with services and products - amount too low")
    public void updateBudgetItemAmount_CalculationWithServicesAndProducts_AmountTooLow() {
        // Same scenario as above but with amount less than used amount
        int newAmount = 600;

        HttpEntity<Integer> request = new HttpEntity<>(newAmount, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + BUDGET_ITEM_WITH_SERVICES_AND_PRODUCTS,
                HttpMethod.PUT,
                request,
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
        assertTrue(response.getBody().contains("New amount cannot be less than the amount already used"));
    }

    @Test
    @DisplayName("Update Budget Item Amount - Budget item with no services or products")
    public void updateBudgetItemAmount_NoServicesOrProducts() {
        int newAmount = 100;

        HttpEntity<Integer> request = new HttpEntity<>(newAmount, getHeadersWithAuth());

        ResponseEntity<UpdatedBudgetItemDTO> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + EXISTING_BUDGET_ITEM_ID,
                HttpMethod.PUT,
                request,
                UpdatedBudgetItemDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(100, response.getBody().getAmount());
    }
    @Test
    @DisplayName("Update Budget Item Amount - Deleted budget item")
    public void updateBudgetItemAmount_DeletedBudgetItem() {
        int newAmount = 1000;
        HttpEntity<Integer> request = new HttpEntity<>(newAmount, getHeadersWithAuth());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + DELETED_BUDGET_ITEM_ID,
                HttpMethod.PUT,
                request,
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
        assertTrue(response.getBody().contains("Cannot update deleted budget item"));
    }
    // ==================== buy() Tests ====================

    @Test
    @DisplayName("Buy Offering - Success")
    public void buyOffering_Success() {
        ResponseEntity<Boolean> response = restTemplate.exchange(
                BASE + "/" + EVENT_WITHOUT_BUDGET_ITEM + "/budget/buy/" + EXISTING_OFFERING_ID,
                HttpMethod.PUT,
                new HttpEntity<>(null, getHeadersWithAuth()),
                Boolean.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    @DisplayName("Buy Offering - Unauthorized")
    public void buyOffering_Unauthorized() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/buy/" + EXISTING_OFFERING_ID,
                HttpMethod.PUT,
                new HttpEntity<>(null),
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Buy Offering - Unauthorized with USER role")
    public void buyOffering_UnauthorizedUserRole() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/buy/" + EXISTING_OFFERING_ID,
                HttpMethod.PUT,
                new HttpEntity<>(null, getHeadersWithAuth(userToken)),
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Buy Offering - Event not found")
    public void buyOffering_EventNotFound() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + NON_EXISTENT_EVENT_ID + "/budget/buy/" + EXISTING_OFFERING_ID,
                HttpMethod.PUT,
                new HttpEntity<>(null, getHeadersWithAuth()),
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Buy Offering - Offering not found")
    public void buyOffering_OfferingNotFound() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/buy/" + NON_EXISTENT_OFFERING_ID,
                HttpMethod.PUT,
                new HttpEntity<>(null, getHeadersWithAuth()),
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Buy Offering - Insufficient budget")
    public void buyOffering_InsufficientBudget() {
        int expensiveOfferingId = 1;
        ResponseEntity<Boolean> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/buy/" + expensiveOfferingId,
                HttpMethod.PUT,
                new HttpEntity<>(null, getHeadersWithAuth()),
                Boolean.class);
        assertFalse(response.getBody());
    }

    @Test
    @DisplayName("Buy Offering - Product already purchased")
    public void buyOffering_ProductAlreadyPurchased() {
        int alreadyPurchasedProductId = 2;
        ResponseEntity<Boolean> response = restTemplate.exchange(
                BASE + "/" + EVENT_WITH_BUDGET_ITEM + "/budget/buy/" + alreadyPurchasedProductId,
                HttpMethod.PUT,
                new HttpEntity<>(null, getHeadersWithAuth()),
                Boolean.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
    }

    @Test
    @DisplayName("Buy Offering - Invalid eventId format")
    public void buyOffering_InvalidEventIdFormat() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/invalid-id/budget/buy/" + EXISTING_OFFERING_ID,
                HttpMethod.PUT,
                new HttpEntity<>(null, getHeadersWithAuth()),
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Buy Offering - Invalid offeringId format")
    public void buyOffering_InvalidOfferingIdFormat() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/buy/invalid-id",
                HttpMethod.PUT,
                new HttpEntity<>(null, getHeadersWithAuth()),
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }
    // ==================== deleteBudgetItem() Tests ====================

    @Test
    @DisplayName("Delete Budget Item - Success")
    public void deleteBudgetItem_Success() {
        ResponseEntity<Boolean> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + BUDGET_ITEM_TO_DELETE,
                HttpMethod.DELETE,
                new HttpEntity<>(null, getHeadersWithAuth()),
                Boolean.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
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
    @DisplayName("Delete Budget Item - Unauthorized with USER role")
    public void deleteBudgetItem_UnauthorizedUserRole() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + BUDGET_ITEM_TO_DELETE,
                HttpMethod.DELETE,
                new HttpEntity<>(null, getHeadersWithAuth(userToken)),
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Delete Budget Item - Budget item not found")
    public void deleteBudgetItem_BudgetItemNotFound() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + NON_EXISTENT_BUDGET_ITEM_ID,
                HttpMethod.DELETE,
                new HttpEntity<>(null, getHeadersWithAuth()),
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Delete Budget Item - Event not found")
    public void deleteBudgetItem_EventNotFound() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/" + NON_EXISTENT_EVENT_ID + "/budget/" + BUDGET_ITEM_TO_DELETE,
                HttpMethod.DELETE,
                new HttpEntity<>(null, getHeadersWithAuth()),
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Delete Budget Item - Cannot delete item with purchases")
    public void deleteBudgetItem_CannotDeleteWithPurchases() {
        ResponseEntity<Boolean> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + BUDGET_ITEM_WITH_SERVICES_AND_PRODUCTS,
                HttpMethod.DELETE,
                new HttpEntity<>(null, getHeadersWithAuth()),
                Boolean.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
    }

    @Test
    @DisplayName("Delete Budget Item - Already deleted item")
    public void deleteBudgetItem_AlreadyDeleted() {
        ResponseEntity<Boolean> response = restTemplate.exchange(
                BASE + "/" + EXISTING_EVENT_ID + "/budget/" + DELETED_BUDGET_ITEM_ID,
                HttpMethod.DELETE,
                new HttpEntity<>(null, getHeadersWithAuth()),
                Boolean.class);

        assertFalse(response.getBody());
    }

    @Test
    @DisplayName("Delete Budget Item - Invalid eventId format")
    public void deleteBudgetItem_InvalidEventIdFormat() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/invalid-id/budget/" + BUDGET_ITEM_TO_DELETE,
                HttpMethod.DELETE,
                new HttpEntity<>(null, getHeadersWithAuth()),
                String.class);

        assertTrue(response.getStatusCode().is4xxClientError());
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
        assertEquals(1930,response.getBody());
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

        assertTrue(response.getStatusCode().is4xxClientError());
        assertTrue(response.getBody().contains("Event with ID"));
    }
    @Test
    @DisplayName("Get Budget Items - Success for existing event")
    public void getBudgetItems_Success() {
        HttpEntity<Void> request = new HttpEntity<>(getHeadersWithAuth());

        ResponseEntity<GetBudgetItemDTO[]> response = restTemplate.exchange(
                BASE + "/budget/" + EVENT_WITH_BUDGET_ITEM,
                HttpMethod.GET,
                request,
                GetBudgetItemDTO[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1,response.getBody().length);

    }

    @Test
    @DisplayName("Get Budget Items - Event has no budget items")
    public void getBudgetItems_EmptyList() {
        HttpEntity<Void> request = new HttpEntity<>(getHeadersWithAuth());

        ResponseEntity<GetBudgetItemDTO[]> response = restTemplate.exchange(
                BASE + "/budget/" + 5, // second event with no budget items
                HttpMethod.GET,
                request,
                GetBudgetItemDTO[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().length);
    }

    @Test
    @DisplayName("Get Budget Items - Non-existent event returns empty list")
    public void getBudgetItems_NonExistentEvent() {
        HttpEntity<Void> request = new HttpEntity<>(getHeadersWithAuth());

        ResponseEntity<GetBudgetItemDTO[]> response = restTemplate.exchange(
                BASE + "/budget/" + NON_EXISTENT_EVENT_ID,
                HttpMethod.GET,
                request,
                GetBudgetItemDTO[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().length);
    }

    @Test
    @DisplayName("Get Budget Items - Unauthorized role")
    public void getBudgetItems_UnauthorizedRole() {
        HttpEntity<Void> request = new HttpEntity<>(getHeadersWithAuth(userToken)); // Not an organizer

        ResponseEntity<String> response = restTemplate.exchange(
                BASE + "/budget/" + EVENT_WITH_BUDGET_ITEM,
                HttpMethod.GET,
                request,
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
