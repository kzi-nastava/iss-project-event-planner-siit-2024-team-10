package com.ftn.iss.eventPlanner.service;

import com.ftn.iss.eventPlanner.dto.budgetitem.*;
import com.ftn.iss.eventPlanner.model.*;
import com.ftn.iss.eventPlanner.repositories.BudgetItemRepository;
import com.ftn.iss.eventPlanner.repositories.EventRepository;
import com.ftn.iss.eventPlanner.repositories.OfferingCategoryRepository;
import com.ftn.iss.eventPlanner.repositories.OfferingRepository;
import com.ftn.iss.eventPlanner.services.BudgetItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetItemServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private BudgetItemRepository budgetItemRepository;

    @Mock
    private OfferingCategoryRepository offeringCategoryRepository;

    @Mock
    private OfferingRepository offeringRepository;

    @Mock
    private ModelMapper modelMapper;
    @Spy
    @InjectMocks
    private BudgetItemService budgetItemService;

    private static final int VALID_EVENT_ID = 1;
    private static final int VALID_CATEGORY_ID = 1;
    private static final int VALID_OFFERING_ID = 1;
    private static final int VALID_BUDGET_ITEM_ID = 1;
    private static final int INVALID_EVENT_ID = 999;
    private static final int INVALID_CATEGORY_ID = 999;
    private static final int INVALID_OFFERING_ID = 999;
    private static final int INVALID_BUDGET_ITEM_ID = 999;

    private Event event;
    private OfferingCategory category;
    private CreateBudgetItemDTO createBudgetItemDTO;
    private BudgetItem budgetItem;
    private CreatedBudgetItemDTO createdBudgetItemDTO;
    private UpdatedBudgetItemDTO updatedBudgetItemDTO;
    private Service service;
    private Product product;
    private ServiceDetails serviceDetails;
    private ProductDetails productDetails;
    private ProductDetails historicalProductDetails;

    @BeforeEach
    void setUp() {
        // Setup Event
        event = new Event();
        event.setId(VALID_EVENT_ID);
        event.setBudget(new HashSet<>());

        // Setup OfferingCategory
        category = new OfferingCategory();
        category.setId(VALID_CATEGORY_ID);

        // Setup CreateBudgetItemDTO
        createBudgetItemDTO = new CreateBudgetItemDTO();
        createBudgetItemDTO.setAmount(100);
        createBudgetItemDTO.setCategoryId(VALID_CATEGORY_ID);

        // Setup BudgetItem
        budgetItem = new BudgetItem();
        budgetItem.setId(VALID_BUDGET_ITEM_ID);
        budgetItem.setAmount(1000);
        budgetItem.setDeleted(false);
        budgetItem.setEvent(event);
        budgetItem.setCategory(category);
        budgetItem.setServices(new HashSet<>());
        budgetItem.setProducts(new HashSet<>());

        // Add budget item to event
        event.getBudget().add(budgetItem);

        // Setup CreatedBudgetItemDTO
        createdBudgetItemDTO = new CreatedBudgetItemDTO();
        createdBudgetItemDTO.setAmount(100);

        // Setup UpdatedBudgetItemDTO
        updatedBudgetItemDTO = new UpdatedBudgetItemDTO();
        updatedBudgetItemDTO.setAmount(1500);

        // Setup Service and ServiceDetails
        serviceDetails = new ServiceDetails();
        serviceDetails.setPrice(500.0);
        serviceDetails.setDiscount(0.0);
        service = new Service();
        service.setCategory(category);
        service.setId(VALID_OFFERING_ID);
        service.setCurrentDetails(serviceDetails);

        // Setup ProductDetails
        productDetails = new ProductDetails();
        productDetails.setId(1);
        productDetails.setPrice(300.0);
        productDetails.setDiscount(0.0);

        // Setup historical ProductDetails
        historicalProductDetails = new ProductDetails();
        historicalProductDetails.setId(2);
        historicalProductDetails.setPrice(250.0);
        historicalProductDetails.setDiscount(0.0);

        // Setup Product
        product = new Product();
        product.setId(VALID_OFFERING_ID);
        product.setCategory(category);
        product.setCurrentDetails(productDetails);
        product.setProductDetailsHistory(new HashSet<>());
        product.getProductDetailsHistory().add(historicalProductDetails);
    }

    @Test
    void create_WhenEventNotFound_ThrowsIllegalArgumentException() {
        // Arrange
        when(eventRepository.findById(INVALID_EVENT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> budgetItemService.create(INVALID_EVENT_ID, createBudgetItemDTO, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event with ID " + INVALID_EVENT_ID + " not found");

        // Verify
        verify(eventRepository).findById(INVALID_EVENT_ID);
        verifyNoInteractions(offeringCategoryRepository, offeringRepository, budgetItemRepository, modelMapper);
    }

    @Test
    void create_WhenCategoryNotFound_ThrowsIllegalArgumentException() {
        // Arrange
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(offeringCategoryRepository.findById(INVALID_CATEGORY_ID)).thenReturn(Optional.empty());

        createBudgetItemDTO.setCategoryId(INVALID_CATEGORY_ID);

        // Act & Assert
        assertThatThrownBy(() -> budgetItemService.create(VALID_EVENT_ID, createBudgetItemDTO, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Offering category with ID " + INVALID_CATEGORY_ID + " not found");

        // Verify
        verify(eventRepository).findById(VALID_EVENT_ID);
        verify(offeringCategoryRepository).findById(INVALID_CATEGORY_ID);
        verifyNoInteractions(offeringRepository, budgetItemRepository, modelMapper);
    }

    @Test
    void create_WhenOfferingNotFound_ThrowsIllegalArgumentException() {
        // Arrange
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(offeringCategoryRepository.findById(VALID_CATEGORY_ID)).thenReturn(Optional.of(category));
        when(offeringRepository.findById(INVALID_OFFERING_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> budgetItemService.create(VALID_EVENT_ID, createBudgetItemDTO, INVALID_OFFERING_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Offering with ID " + INVALID_OFFERING_ID + " not found");

        // Verify
        verify(eventRepository).findById(VALID_EVENT_ID);
        verify(offeringCategoryRepository).findById(VALID_CATEGORY_ID);
        verify(offeringRepository).findById(INVALID_OFFERING_ID);
        verifyNoInteractions(budgetItemRepository, modelMapper);
    }

    @Test
    void create_WithoutOffering_CreatesBasicBudgetItem() {
        // Arrange
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(offeringCategoryRepository.findById(VALID_CATEGORY_ID)).thenReturn(Optional.of(category));
        when(budgetItemRepository.save(any(BudgetItem.class))).thenReturn(budgetItem);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(modelMapper.map(budgetItem, CreatedBudgetItemDTO.class)).thenReturn(createdBudgetItemDTO);

        // Act
        CreatedBudgetItemDTO result = budgetItemService.create(VALID_EVENT_ID, createBudgetItemDTO, 0);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(100);

        // Verify
        verify(eventRepository).findById(VALID_EVENT_ID);
        verify(offeringCategoryRepository).findById(VALID_CATEGORY_ID);
        verify(budgetItemRepository).save(any(BudgetItem.class));
        verify(eventRepository).save(event);
        verify(modelMapper).map(budgetItem, CreatedBudgetItemDTO.class);
        verifyNoInteractions(offeringRepository);
    }

    @Test
    void create_WithServiceOffering_CreatesServiceBudgetItem() {
        // Arrange
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(offeringCategoryRepository.findById(VALID_CATEGORY_ID)).thenReturn(Optional.of(category));
        when(offeringRepository.findById(VALID_OFFERING_ID)).thenReturn(Optional.of(service));
        when(budgetItemRepository.save(any(BudgetItem.class))).thenReturn(budgetItem);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(modelMapper.map(budgetItem, CreatedBudgetItemDTO.class)).thenReturn(createdBudgetItemDTO);

        // Act
        CreatedBudgetItemDTO result = budgetItemService.create(VALID_EVENT_ID, createBudgetItemDTO, VALID_OFFERING_ID);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(100);

        // Verify
        verify(eventRepository).findById(VALID_EVENT_ID);
        verify(offeringCategoryRepository).findById(VALID_CATEGORY_ID);
        verify(offeringRepository).findById(VALID_OFFERING_ID);
        verify(budgetItemRepository).save(any(BudgetItem.class));
        verify(eventRepository).save(event);
        verify(modelMapper).map(budgetItem, CreatedBudgetItemDTO.class);

        // Verify that the service details were added to the budget item
        verify(budgetItemRepository).save(argThat(item ->
                item.getServices().contains(serviceDetails)));
    }

    @Test
    void create_WithProductOffering_CreatesProductBudgetItem() {
        // Arrange
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(offeringCategoryRepository.findById(VALID_CATEGORY_ID)).thenReturn(Optional.of(category));
        when(offeringRepository.findById(VALID_OFFERING_ID)).thenReturn(Optional.of(product));
        when(budgetItemRepository.save(any(BudgetItem.class))).thenReturn(budgetItem);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(modelMapper.map(budgetItem, CreatedBudgetItemDTO.class)).thenReturn(createdBudgetItemDTO);

        // Act
        CreatedBudgetItemDTO result = budgetItemService.create(VALID_EVENT_ID, createBudgetItemDTO, VALID_OFFERING_ID);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(100);

        // Verify
        verify(eventRepository).findById(VALID_EVENT_ID);
        verify(offeringCategoryRepository).findById(VALID_CATEGORY_ID);
        verify(offeringRepository).findById(VALID_OFFERING_ID);
        verify(budgetItemRepository).save(any(BudgetItem.class));
        verify(eventRepository).save(event);
        verify(modelMapper).map(budgetItem, CreatedBudgetItemDTO.class);

        // Verify that the product details were added to the budget item
        verify(budgetItemRepository).save(argThat(item ->
                item.getProducts().contains(productDetails)));
    }

    @Test
    void create_ValidRequest_SetsCorrectBudgetItemProperties() {
        // Arrange
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(offeringCategoryRepository.findById(VALID_CATEGORY_ID)).thenReturn(Optional.of(category));
        when(budgetItemRepository.save(any(BudgetItem.class))).thenReturn(budgetItem);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(modelMapper.map(budgetItem, CreatedBudgetItemDTO.class)).thenReturn(createdBudgetItemDTO);

        // Act
        budgetItemService.create(VALID_EVENT_ID, createBudgetItemDTO, 0);

        // Assert
        verify(budgetItemRepository).save(argThat(item ->
                item.getAmount() == 100 &&
                        !item.isDeleted() &&
                        item.getEvent().equals(event) &&
                        item.getCategory().equals(category)
        ));
    }

    @Test
    void create_ValidRequest_AddsItemToEventBudget() {
        // Arrange
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(offeringCategoryRepository.findById(VALID_CATEGORY_ID)).thenReturn(Optional.of(category));
        when(budgetItemRepository.save(any(BudgetItem.class))).thenReturn(budgetItem);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(modelMapper.map(budgetItem, CreatedBudgetItemDTO.class)).thenReturn(createdBudgetItemDTO);

        // Act
        budgetItemService.create(VALID_EVENT_ID, createBudgetItemDTO, 0);

        // Assert
        verify(eventRepository).save(argThat(savedEvent ->
                savedEvent.getBudget().contains(budgetItem)
        ));
    }

    @Test
    void buy_WhenEventNotFound_ThrowsIllegalArgumentException() {
        // Arrange
        when(eventRepository.findById(INVALID_EVENT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> budgetItemService.buy(INVALID_EVENT_ID, VALID_OFFERING_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event with ID " + INVALID_EVENT_ID + " not found");

        // Verify
        verify(eventRepository).findById(INVALID_EVENT_ID);
        verifyNoInteractions(offeringRepository);
    }

    @Test
    void buy_WhenOfferingNotFound_ThrowsIllegalArgumentException() {
        // Arrange
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(offeringRepository.findById(INVALID_OFFERING_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> budgetItemService.buy(VALID_EVENT_ID, INVALID_OFFERING_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Offering with ID " + INVALID_OFFERING_ID + " not found");

        // Verify
        verify(eventRepository).findById(VALID_EVENT_ID);
        verify(offeringRepository).findById(INVALID_OFFERING_ID);
    }

    @Test
    void buy_WhenServiceCanBeAfforded_ReturnsTrue() {
        // Arrange
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(offeringRepository.findById(VALID_OFFERING_ID)).thenReturn(Optional.of(service));

        doReturn(true)
                .when(budgetItemService)
                .hasMoneyLeft(any(BudgetItem.class), eq(500.0), eq(0.0));

        // Act
        budgetItemService.buy(VALID_EVENT_ID, VALID_OFFERING_ID);

        // Assert
        assertThat(budgetItem.getServices()).contains(serviceDetails);
    }

    @Test
    void buy_WhenServiceCannotBeAfforded_ThrowsException() {
        // Arrange
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(offeringRepository.findById(VALID_OFFERING_ID)).thenReturn(Optional.of(service));
        when(budgetItemService.hasMoneyLeft(budgetItem, 500.0, 0.0)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> budgetItemService.buy(VALID_EVENT_ID, VALID_OFFERING_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Insufficient budget for this purchase");

        assertThat(budgetItem.getServices()).doesNotContain(serviceDetails);
    }

    @Test
    void buy_WhenProductCanBeAfforded_ReturnsTrue() {
        // Arrange
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(offeringRepository.findById(VALID_OFFERING_ID)).thenReturn(Optional.of(product));

        when(budgetItemService.hasMoneyLeft(budgetItem, 300.0, 0.0)).thenReturn(true);

        // Act
        budgetItemService.buy(VALID_EVENT_ID, VALID_OFFERING_ID);

        // Assert
        assertThat(budgetItem.getProducts()).contains(productDetails);
    }

    @Test
    void buy_WhenProductCannotBeAfforded_ThrowsException() {
        // Arrange
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(offeringRepository.findById(VALID_OFFERING_ID)).thenReturn(Optional.of(product));
        when(budgetItemService.hasMoneyLeft(budgetItem, 300.0, 0.0)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> budgetItemService.buy(VALID_EVENT_ID, VALID_OFFERING_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Insufficient budget for this purchase");

        assertThat(budgetItem.getProducts()).doesNotContain(productDetails);
    }

    @Test
    void buy_WhenProductAlreadyAdded_ThrowsIllegalArgumentException() {
        // Arrange
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(offeringRepository.findById(VALID_OFFERING_ID)).thenReturn(Optional.of(product));

        // Add product to budget item first
        budgetItem.getProducts().add(productDetails);

        // Act
        // Assert
        assertThatThrownBy(() -> budgetItemService.buy(VALID_EVENT_ID, VALID_OFFERING_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product already purchased");
    }

    @Test
    void buy_WhenHistoricalProductAlreadyAdded_ThrowsException() {
        // Arrange
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(offeringRepository.findById(VALID_OFFERING_ID)).thenReturn(Optional.of(product));

        // Add historical product to budget item first
        budgetItem.getProducts().add(historicalProductDetails);

        // Act & Assert
        assertThatThrownBy(() -> budgetItemService.buy(VALID_EVENT_ID, VALID_OFFERING_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product already purchased");

        assertThat(budgetItem.getProducts()).containsExactly(historicalProductDetails);
    }

    @Test
    void buy_WhenNoBudgetItemForCategory_CreatesNewBudgetItem() {
        // Arrange
        // Create event with empty budget
        Event emptyEvent = new Event();
        emptyEvent.setId(VALID_EVENT_ID);
        emptyEvent.setBudget(new HashSet<>());

        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(emptyEvent));
        when(offeringRepository.findById(VALID_OFFERING_ID)).thenReturn(Optional.of(service));

        CreatedBudgetItemDTO mockCreatedDTO = new CreatedBudgetItemDTO();
        doReturn(mockCreatedDTO)
                .when(budgetItemService)
                .create(eq(VALID_EVENT_ID), any(CreateBudgetItemDTO.class), eq(VALID_OFFERING_ID));

        // Act
        budgetItemService.buy(VALID_EVENT_ID, VALID_OFFERING_ID);

        // Assert
        verify(budgetItemService).create(eq(VALID_EVENT_ID), argThat(dto ->
                dto.getAmount() == 0 && dto.getCategoryId() == VALID_CATEGORY_ID), eq(VALID_OFFERING_ID));
    }

    @Test
    void buy_WhenDifferentCategoryExists_CreatesNewBudgetItem() {
        // Arrange
        // Create a different category
        OfferingCategory differentCategory = new OfferingCategory();
        differentCategory.setId(2);

        // Update the offering to use different category
        service.setCategory(differentCategory);

        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(offeringRepository.findById(VALID_OFFERING_ID)).thenReturn(Optional.of(service));

        CreatedBudgetItemDTO mockCreatedDTO = new CreatedBudgetItemDTO();
        doReturn(mockCreatedDTO)
                .when(budgetItemService)
                .create(eq(VALID_EVENT_ID), any(CreateBudgetItemDTO.class), eq(VALID_OFFERING_ID));

        // Act
        budgetItemService.buy(VALID_EVENT_ID, VALID_OFFERING_ID);

        // Assert
        verify(budgetItemService).create(eq(VALID_EVENT_ID), argThat(dto ->
                dto.getAmount() == 0 && dto.getCategoryId() == 2), eq(VALID_OFFERING_ID));
    }

    @Test
    void buy_WhenMultipleServicesAddedToSameCategory_AddsAllServices() {
        // Arrange
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(offeringRepository.findById(VALID_OFFERING_ID)).thenReturn(Optional.of(service));

        // Add a service first
        ServiceDetails existingService = new ServiceDetails();
        existingService.setId(2);
        budgetItem.getServices().add(existingService);

        when(budgetItemService.hasMoneyLeft(budgetItem, 500.0, 0.0)).thenReturn(true);

        // Act
        budgetItemService.buy(VALID_EVENT_ID, VALID_OFFERING_ID);

        // Assert
        assertThat(budgetItem.getServices()).hasSize(2);
        assertThat(budgetItem.getServices()).contains(existingService, serviceDetails);
    }

    @Test
    void buy_HandlesDiscountedPrice() {
        // Arrange
        serviceDetails.setDiscount(0.2); // 20% discount

        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(offeringRepository.findById(VALID_OFFERING_ID)).thenReturn(Optional.of(service));

        when(budgetItemService.hasMoneyLeft(budgetItem, 500.0, 0.2)).thenReturn(true);

        // Act
        budgetItemService.buy(VALID_EVENT_ID, VALID_OFFERING_ID);

        // Assert
        verify(budgetItemService).hasMoneyLeft(budgetItem, 500.0, 0.2);
    }
    @Test
    void findAll_WhenCalled_ReturnsListOfGetBudgetItemDTO() {
        // Arrange
        BudgetItem item1 = new BudgetItem();
        item1.setId(1);
        BudgetItem item2 = new BudgetItem();
        item2.setId(2);

        GetBudgetItemDTO dto1 = new GetBudgetItemDTO();
        dto1.setId(1);
        GetBudgetItemDTO dto2 = new GetBudgetItemDTO();
        dto2.setId(2);

        when(budgetItemRepository.findAll()).thenReturn(List.of(item1, item2));
        when(modelMapper.map(item1, GetBudgetItemDTO.class)).thenReturn(dto1);
        when(modelMapper.map(item2, GetBudgetItemDTO.class)).thenReturn(dto2);

        // Act
        List<GetBudgetItemDTO> result = budgetItemService.findAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dto1, dto2);

        verify(budgetItemRepository).findAll();
        verify(modelMapper).map(item1, GetBudgetItemDTO.class);
        verify(modelMapper).map(item2, GetBudgetItemDTO.class);
    }

    @Test
    void findById_WhenBudgetItemDoesNotExist_ThrowsIllegalArgumentException() {
        // Arrange
        when(budgetItemRepository.findById(INVALID_BUDGET_ITEM_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> budgetItemService.findById(INVALID_BUDGET_ITEM_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Budget item with ID " + INVALID_BUDGET_ITEM_ID + " not found");

        verify(budgetItemRepository).findById(INVALID_BUDGET_ITEM_ID);
        verifyNoMoreInteractions(budgetItemRepository, modelMapper);
    }
    @Test
    public void findById_WhenBudgetItemExists_ReturnsGetBudgetItemDTO() {
        // Arrange
        when(budgetItemRepository.findById(VALID_BUDGET_ITEM_ID)).thenReturn(Optional.of(budgetItem));

        // Act
        GetBudgetItemDTO result = budgetItemService.findById(VALID_BUDGET_ITEM_ID);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(VALID_BUDGET_ITEM_ID);

        verify(budgetItemRepository).findById(VALID_BUDGET_ITEM_ID);
        verifyNoMoreInteractions(budgetItemRepository);
    }

    @Test
    void updateAmount_ValidAmount_Success() {
        // Given
        int newAmount = 1500;
        when(budgetItemRepository.findById(VALID_BUDGET_ITEM_ID)).thenReturn(Optional.of(budgetItem));
        when(budgetItemRepository.save(budgetItem)).thenReturn(budgetItem);
        when(modelMapper.map(budgetItem, UpdatedBudgetItemDTO.class)).thenReturn(updatedBudgetItemDTO);

        // When
        UpdatedBudgetItemDTO result = budgetItemService.updateAmount(VALID_BUDGET_ITEM_ID, new UpdateBudgetItemDTO(newAmount));

        // Then
        assertThat(result).isNotNull();
        assertThat(budgetItem.getAmount()).isEqualTo(newAmount);
        verify(budgetItemRepository).findById(VALID_BUDGET_ITEM_ID);
        verify(budgetItemRepository).save(budgetItem);
        verify(modelMapper).map(budgetItem, UpdatedBudgetItemDTO.class);
    }

    @Test
    void updateAmount_BudgetItemNotFound_ThrowsException() {
        // Given
        when(budgetItemRepository.findById(INVALID_BUDGET_ITEM_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> budgetItemService.updateAmount(INVALID_BUDGET_ITEM_ID,new UpdateBudgetItemDTO(1500)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Budget item with ID " + INVALID_BUDGET_ITEM_ID + " not found");

        verify(budgetItemRepository).findById(INVALID_BUDGET_ITEM_ID);
        verify(budgetItemRepository, never()).save(any());
    }

    @Test
    void updateAmount_NewAmountLessThanUsed_ThrowsException() {
        // Given
        ServiceDetails expensiveService = new ServiceDetails();
        expensiveService.setPrice(800.0);
        expensiveService.setDiscount(10.0); // 10% discount
        budgetItem.getServices().add(expensiveService);

        ProductDetails expensiveProduct = new ProductDetails();
        expensiveProduct.setPrice(400.0);
        expensiveProduct.setDiscount(5.0); // 5% discount
        budgetItem.getProducts().add(expensiveProduct);

        // Used amount = 800 * 0.9 + 400 * 0.95 = 720 + 380 = 1100
        int newAmount = 1000; // Less than used amount

        when(budgetItemRepository.findById(VALID_BUDGET_ITEM_ID)).thenReturn(Optional.of(budgetItem));

        assertThatThrownBy(() -> budgetItemService.updateAmount(VALID_BUDGET_ITEM_ID, new UpdateBudgetItemDTO(newAmount)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("New amount cannot be less than the amount already used");

        verify(budgetItemRepository).findById(VALID_BUDGET_ITEM_ID);
        verify(budgetItemRepository, never()).save(any());
    }

    @Test
    void updateAmount_NewAmountEqualsUsed_Success() {
        // Given
        ServiceDetails service = new ServiceDetails();
        service.setPrice(500.0);
        service.setDiscount(20.0); // 20% discount
        budgetItem.getServices().add(service);

        // Used amount = 500 * 0.8 = 400
        int newAmount = 400;

        when(budgetItemRepository.findById(VALID_BUDGET_ITEM_ID)).thenReturn(Optional.of(budgetItem));
        when(budgetItemRepository.save(budgetItem)).thenReturn(budgetItem);
        when(modelMapper.map(budgetItem, UpdatedBudgetItemDTO.class)).thenReturn(updatedBudgetItemDTO);

        // When
        UpdatedBudgetItemDTO result = budgetItemService.updateAmount(VALID_BUDGET_ITEM_ID, new UpdateBudgetItemDTO(newAmount));

        // Then
        assertThat(result).isNotNull();
        assertThat(budgetItem.getAmount()).isEqualTo(newAmount);
        verify(budgetItemRepository).save(budgetItem);
    }

    // ==================== delete() Tests ====================

    @Test
    void delete_NoServicesOrProducts_Success() {
        // Given
        when(budgetItemRepository.findById(VALID_BUDGET_ITEM_ID)).thenReturn(Optional.of(budgetItem));
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(eventRepository.save(event)).thenReturn(event);
        when(budgetItemRepository.save(budgetItem)).thenReturn(budgetItem);

        // When
        budgetItemService.delete(VALID_EVENT_ID, VALID_BUDGET_ITEM_ID);

        // Then
        assertThat(budgetItem.isDeleted()).isTrue();
        assertThat(event.getBudget()).doesNotContain(budgetItem);
        verify(budgetItemRepository).findById(VALID_BUDGET_ITEM_ID);
        verify(eventRepository).findById(VALID_EVENT_ID);
        verify(eventRepository).save(event);
        verify(budgetItemRepository).save(budgetItem);
    }

    @Test
    void delete_HasServices_ReturnsFalse() {
        // Given
        budgetItem.getServices().add(serviceDetails);
        when(budgetItemRepository.findById(VALID_BUDGET_ITEM_ID)).thenReturn(Optional.of(budgetItem));

        // When
        budgetItemService.delete(VALID_EVENT_ID, VALID_BUDGET_ITEM_ID);

        // Then
        verify(budgetItemRepository).findById(VALID_BUDGET_ITEM_ID);
        verify(eventRepository, never()).findById(any());
        verify(budgetItemRepository, never()).save(any());
    }

    @Test
    void delete_HasProducts_ReturnsFalse() {
        // Given
        budgetItem.getProducts().add(productDetails);
        when(budgetItemRepository.findById(VALID_BUDGET_ITEM_ID)).thenReturn(Optional.of(budgetItem));

        // When
        budgetItemService.delete(VALID_EVENT_ID, VALID_BUDGET_ITEM_ID);

        // Then
        verify(budgetItemRepository).findById(VALID_BUDGET_ITEM_ID);
        verify(eventRepository, never()).findById(any());
        verify(budgetItemRepository, never()).save(any());
    }

    @Test
    void delete_HasServicesAndProducts_ReturnsFalse() {
        // Given
        budgetItem.getServices().add(serviceDetails);
        budgetItem.getProducts().add(productDetails);
        when(budgetItemRepository.findById(VALID_BUDGET_ITEM_ID)).thenReturn(Optional.of(budgetItem));

        // When
        budgetItemService.delete(VALID_EVENT_ID, VALID_BUDGET_ITEM_ID);

        // Then
        verify(budgetItemRepository).findById(VALID_BUDGET_ITEM_ID);
        verify(eventRepository, never()).findById(any());
        verify(budgetItemRepository, never()).save(any());
    }

    @Test
    void delete_BudgetItemNotFound_ThrowsException() {
        // Given
        when(budgetItemRepository.findById(INVALID_BUDGET_ITEM_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> budgetItemService.delete(VALID_BUDGET_ITEM_ID, INVALID_BUDGET_ITEM_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Budget item with ID " + INVALID_BUDGET_ITEM_ID + " not found");

        verify(budgetItemRepository).findById(INVALID_BUDGET_ITEM_ID);
        verify(eventRepository, never()).findById(any());
    }

    @Test
    void delete_EventNotFound_ThrowsException() {
        // Given
        when(budgetItemRepository.findById(VALID_BUDGET_ITEM_ID)).thenReturn(Optional.of(budgetItem));
        when(eventRepository.findById(INVALID_EVENT_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> budgetItemService.delete(INVALID_BUDGET_ITEM_ID, VALID_BUDGET_ITEM_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Event with ID " + INVALID_BUDGET_ITEM_ID + " not found");

        verify(budgetItemRepository).findById(VALID_BUDGET_ITEM_ID);
        verify(eventRepository).findById(INVALID_EVENT_ID);
        verify(budgetItemRepository, never()).save(any());
    }

    // ==================== hasMoneyLeft() Tests ====================

    @Test
    void hasMoneyLeft_EnoughMoney_ReturnsTrue() {
        // Given
        budgetItem.setAmount(1000);
        double price = 200.0;
        double discount = 10.0; // 10% discount

        // When
        boolean result = budgetItemService.hasMoneyLeft(budgetItem, price, discount);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void hasMoneyLeft_NotEnoughMoney_ReturnsFalse() {
        // Given
        budgetItem.setAmount(100);
        double price = 200.0;
        double discount = 0.0; // No discount

        // When
        boolean result = budgetItemService.hasMoneyLeft(budgetItem, price, discount);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void hasMoneyLeft_ExactAmount_ReturnsTrue() {
        // Given
        budgetItem.setAmount(200);
        double price = 200.0;
        double discount = 0.0; // No discount

        // When
        boolean result = budgetItemService.hasMoneyLeft(budgetItem, price, discount);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void hasMoneyLeft_WithExistingServices_CalculatesCorrectly() {
        // Given
        budgetItem.setAmount(1000);

        ServiceDetails service1 = new ServiceDetails();
        service1.setPrice(300.0);
        service1.setDiscount(10.0); // 10% discount, cost = 270

        ServiceDetails service2 = new ServiceDetails();
        service2.setPrice(200.0);
        service2.setDiscount(0.0); // No discount, cost = 200

        budgetItem.getServices().add(service1);
        budgetItem.getServices().add(service2);

        // Remaining = 1000 - 270 - 200 = 530
        double price = 400.0;
        double discount = 20.0; // 20% discount, cost = 320

        // When
        boolean result = budgetItemService.hasMoneyLeft(budgetItem, price, discount);

        // Then
        assertThat(result).isTrue(); // 530 - 320 = 210 > 0
    }

    @Test
    void hasMoneyLeft_WithExistingProducts_CalculatesCorrectly() {
        // Given
        budgetItem.setAmount(1000);

        ProductDetails product1 = new ProductDetails();
        product1.setPrice(250.0);
        product1.setDiscount(20.0); // 20% discount, cost = 200

        ProductDetails product2 = new ProductDetails();
        product2.setPrice(150.0);
        product2.setDiscount(0.0); // No discount, cost = 150

        budgetItem.getProducts().add(product1);
        budgetItem.getProducts().add(product2);

        // Remaining = 1000 - 200 - 150 = 650
        double price = 700.0;
        double discount = 10.0; // 10% discount, cost = 630

        // When
        boolean result = budgetItemService.hasMoneyLeft(budgetItem, price, discount);

        // Then
        assertThat(result).isTrue(); // 650 - 630 = 20 > 0
    }

    @Test
    void hasMoneyLeft_WithServicesAndProducts_CalculatesCorrectly() {
        // Given
        budgetItem.setAmount(1000);

        ServiceDetails service = new ServiceDetails();
        service.setPrice(300.0);
        service.setDiscount(0.0); // No discount, cost = 300

        ProductDetails product = new ProductDetails();
        product.setPrice(200.0);
        product.setDiscount(25.0); // 25% discount, cost = 150

        budgetItem.getServices().add(service);
        budgetItem.getProducts().add(product);

        // Remaining = 1000 - 300 - 150 = 550
        double price = 600.0;
        double discount = 15.0; // 15% discount, cost = 510

        // When
        boolean result = budgetItemService.hasMoneyLeft(budgetItem, price, discount);

        // Then
        assertThat(result).isTrue(); // 550 - 510 = 40 > 0
    }

    @Test
    void hasMoneyLeft_ZeroDiscount_CalculatesCorrectly() {
        // Given
        budgetItem.setAmount(500);
        double price = 300.0;
        double discount = 0.0; // No discount

        // When
        boolean result = budgetItemService.hasMoneyLeft(budgetItem, price, discount);

        // Then
        assertThat(result).isTrue(); // 500 - 300 = 200 > 0
    }

    @Test
    void hasMoneyLeft_FullDiscount_CalculatesCorrectly() {
        // Given
        budgetItem.setAmount(100);
        double price = 1000.0;
        double discount = 100.0; // 100% discount, cost = 0

        // When
        boolean result = budgetItemService.hasMoneyLeft(budgetItem, price, discount);

        // Then
        assertThat(result).isTrue(); // 100 - 0 = 100 > 0
    }

    @Test
    void hasMoneyLeft_NegativeRemaining_ReturnsFalse() {
        // Given
        budgetItem.setAmount(100);

        ServiceDetails service = new ServiceDetails();
        service.setPrice(80.0);
        service.setDiscount(0.0); // No discount, cost = 80
        budgetItem.getServices().add(service);

        // Remaining = 100 - 80 = 20
        double price = 50.0;
        double discount = 0.0; // No discount, cost = 50

        // When
        boolean result = budgetItemService.hasMoneyLeft(budgetItem, price, discount);

        // Then
        assertThat(result).isFalse(); // 20 - 50 = -30 < 0
    }
    // ==================== getTotalBudgetForEvent() Tests ====================

    @Test
    void getTotalBudgetForEvent_WhenEventNotFound_ThrowsIllegalArgumentException() {
        // Arrange
        when(eventRepository.findById(INVALID_EVENT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> budgetItemService.getTotalBudgetForEvent(INVALID_EVENT_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event with ID " + INVALID_EVENT_ID + " not found");

        // Verify
        verify(eventRepository).findById(INVALID_EVENT_ID);
    }

    @Test
    void getTotalBudgetForEvent_WhenEventHasNoBudgetItems_ReturnsZero() {
        // Arrange
        Event eventWithEmptyBudget = new Event();
        eventWithEmptyBudget.setId(VALID_EVENT_ID);
        eventWithEmptyBudget.setBudget(new HashSet<>());

        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(eventWithEmptyBudget));

        // Act
        double result = budgetItemService.getTotalBudgetForEvent(VALID_EVENT_ID);

        // Assert
        assertThat(result).isEqualTo(0.0);
        verify(eventRepository).findById(VALID_EVENT_ID);
    }

    @Test
    void getTotalBudgetForEvent_WhenEventHasSingleBudgetItem_ReturnsCorrectAmount() {
        // Arrange
        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));

        // Act
        double result = budgetItemService.getTotalBudgetForEvent(VALID_EVENT_ID);

        // Assert
        assertThat(result).isEqualTo(1000.0); // budgetItem has amount 1000
        verify(eventRepository).findById(VALID_EVENT_ID);
    }

    @Test
    void getTotalBudgetForEvent_WhenEventHasMultipleBudgetItems_ReturnsSumOfAmounts() {
        // Arrange
        BudgetItem budgetItem2 = new BudgetItem();
        budgetItem2.setId(2);
        budgetItem2.setAmount(500);
        budgetItem2.setDeleted(false);

        BudgetItem budgetItem3 = new BudgetItem();
        budgetItem3.setId(3);
        budgetItem3.setAmount(300);
        budgetItem3.setDeleted(false);

        event.getBudget().add(budgetItem2);
        event.getBudget().add(budgetItem3);

        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));

        // Act
        double result = budgetItemService.getTotalBudgetForEvent(VALID_EVENT_ID);

        // Assert
        assertThat(result).isEqualTo(1800.0); // 1000 + 500 + 300
        verify(eventRepository).findById(VALID_EVENT_ID);
    }

    @Test
    void getTotalBudgetForEvent_WhenEventHasDeletedBudgetItems_ExcludesDeletedItems() {
        // Arrange
        BudgetItem deletedBudgetItem = new BudgetItem();
        deletedBudgetItem.setId(2);
        deletedBudgetItem.setAmount(500);
        deletedBudgetItem.setDeleted(true); // This item is deleted

        BudgetItem activeBudgetItem = new BudgetItem();
        activeBudgetItem.setId(3);
        activeBudgetItem.setAmount(300);
        activeBudgetItem.setDeleted(false);

        event.getBudget().add(deletedBudgetItem);
        event.getBudget().add(activeBudgetItem);

        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));

        // Act
        double result = budgetItemService.getTotalBudgetForEvent(VALID_EVENT_ID);

        // Assert
        assertThat(result).isEqualTo(1300.0); // 1000 + 300 (excluded deleted item with 500)
        verify(eventRepository).findById(VALID_EVENT_ID);
    }

    @Test
    void getTotalBudgetForEvent_WhenAllBudgetItemsAreDeleted_ReturnsZero() {
        // Arrange
        budgetItem.setDeleted(true);

        BudgetItem deletedBudgetItem2 = new BudgetItem();
        deletedBudgetItem2.setId(2);
        deletedBudgetItem2.setAmount(500);
        deletedBudgetItem2.setDeleted(true);

        event.getBudget().add(deletedBudgetItem2);

        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));

        // Act
        double result = budgetItemService.getTotalBudgetForEvent(VALID_EVENT_ID);

        // Assert
        assertThat(result).isEqualTo(0.0);
        verify(eventRepository).findById(VALID_EVENT_ID);
    }

    @Test
    void getTotalBudgetForEvent_WhenEventHasZeroAmountBudgetItems_ReturnsZero() {
        // Arrange
        budgetItem.setAmount(0);

        BudgetItem zeroBudgetItem = new BudgetItem();
        zeroBudgetItem.setId(2);
        zeroBudgetItem.setAmount(0);
        zeroBudgetItem.setDeleted(false);

        event.getBudget().add(zeroBudgetItem);

        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));

        // Act
        double result = budgetItemService.getTotalBudgetForEvent(VALID_EVENT_ID);

        // Assert
        assertThat(result).isEqualTo(0.0);
        verify(eventRepository).findById(VALID_EVENT_ID);
    }

// ==================== findByEventId() Tests ====================

    @Test
    void findByEventId_WhenNoBudgetItemsExist_ReturnsEmptyList() {
        // Arrange
        when(budgetItemRepository.findByEventId(VALID_EVENT_ID)).thenReturn(List.of());

        // Act
        List<GetBudgetItemDTO> result = budgetItemService.findByEventId(VALID_EVENT_ID);

        // Assert
        assertThat(result).isEmpty();
        verify(budgetItemRepository).findByEventId(VALID_EVENT_ID);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void findByEventId_WhenSingleBudgetItemExists_ReturnsListWithOneDTO() {
        // Arrange
        GetBudgetItemDTO budgetItemDTO = new GetBudgetItemDTO();
        budgetItemDTO.setId(VALID_BUDGET_ITEM_ID);

        when(budgetItemRepository.findByEventId(VALID_EVENT_ID)).thenReturn(List.of(budgetItem));
        doReturn(budgetItemDTO).when(budgetItemService).mapBudgetItemToDTO(budgetItem);

        // Act
        List<GetBudgetItemDTO> result = budgetItemService.findByEventId(VALID_EVENT_ID);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(VALID_BUDGET_ITEM_ID);
        verify(budgetItemRepository).findByEventId(VALID_EVENT_ID);
        verify(budgetItemService).mapBudgetItemToDTO(budgetItem);
    }

    @Test
    void findByEventId_WhenMultipleBudgetItemsExist_ReturnsListWithMultipleDTOs() {
        // Arrange
        BudgetItem budgetItem2 = new BudgetItem();
        budgetItem2.setId(2);
        budgetItem2.setDeleted(false);

        BudgetItem budgetItem3 = new BudgetItem();
        budgetItem3.setId(3);
        budgetItem3.setDeleted(false);

        GetBudgetItemDTO budgetItemDTO1 = new GetBudgetItemDTO();
        budgetItemDTO1.setId(VALID_BUDGET_ITEM_ID);

        GetBudgetItemDTO budgetItemDTO2 = new GetBudgetItemDTO();
        budgetItemDTO2.setId(2);

        GetBudgetItemDTO budgetItemDTO3 = new GetBudgetItemDTO();
        budgetItemDTO3.setId(3);

        when(budgetItemRepository.findByEventId(VALID_EVENT_ID))
                .thenReturn(List.of(budgetItem, budgetItem2, budgetItem3));
        doReturn(budgetItemDTO1).when(budgetItemService).mapBudgetItemToDTO(budgetItem);
        doReturn(budgetItemDTO2).when(budgetItemService).mapBudgetItemToDTO(budgetItem2);
        doReturn(budgetItemDTO3).when(budgetItemService).mapBudgetItemToDTO(budgetItem3);

        // Act
        List<GetBudgetItemDTO> result = budgetItemService.findByEventId(VALID_EVENT_ID);

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getId()).isEqualTo(VALID_BUDGET_ITEM_ID);
        assertThat(result.get(1).getId()).isEqualTo(2);
        assertThat(result.get(2).getId()).isEqualTo(3);
        verify(budgetItemRepository).findByEventId(VALID_EVENT_ID);
        verify(budgetItemService).mapBudgetItemToDTO(budgetItem);
        verify(budgetItemService).mapBudgetItemToDTO(budgetItem2);
        verify(budgetItemService).mapBudgetItemToDTO(budgetItem3);
    }

    @Test
    void findByEventId_WhenBudgetItemsContainDeletedItems_ExcludesDeletedItems() {
        // Arrange
        BudgetItem deletedBudgetItem = new BudgetItem();
        deletedBudgetItem.setId(2);
        deletedBudgetItem.setDeleted(true);

        BudgetItem activeBudgetItem = new BudgetItem();
        activeBudgetItem.setId(3);
        activeBudgetItem.setDeleted(false);

        GetBudgetItemDTO budgetItemDTO1 = new GetBudgetItemDTO();
        budgetItemDTO1.setId(VALID_BUDGET_ITEM_ID);

        GetBudgetItemDTO budgetItemDTO3 = new GetBudgetItemDTO();
        budgetItemDTO3.setId(3);

        when(budgetItemRepository.findByEventId(VALID_EVENT_ID))
                .thenReturn(List.of(budgetItem, deletedBudgetItem, activeBudgetItem));
        doReturn(budgetItemDTO1).when(budgetItemService).mapBudgetItemToDTO(budgetItem);
        doReturn(budgetItemDTO3).when(budgetItemService).mapBudgetItemToDTO(activeBudgetItem);

        // Act
        List<GetBudgetItemDTO> result = budgetItemService.findByEventId(VALID_EVENT_ID);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(VALID_BUDGET_ITEM_ID);
        assertThat(result.get(1).getId()).isEqualTo(3);
        verify(budgetItemRepository).findByEventId(VALID_EVENT_ID);
        verify(budgetItemService).mapBudgetItemToDTO(budgetItem);
        verify(budgetItemService).mapBudgetItemToDTO(activeBudgetItem);
        verify(budgetItemService, never()).mapBudgetItemToDTO(deletedBudgetItem);
    }

    @Test
    void findByEventId_WhenAllBudgetItemsAreDeleted_ReturnsEmptyList() {
        // Arrange
        budgetItem.setDeleted(true);

        BudgetItem deletedBudgetItem2 = new BudgetItem();
        deletedBudgetItem2.setId(2);
        deletedBudgetItem2.setDeleted(true);

        when(budgetItemRepository.findByEventId(VALID_EVENT_ID))
                .thenReturn(List.of(budgetItem, deletedBudgetItem2));

        // Act
        List<GetBudgetItemDTO> result = budgetItemService.findByEventId(VALID_EVENT_ID);

        // Assert
        assertThat(result).isEmpty();
        verify(budgetItemRepository).findByEventId(VALID_EVENT_ID);
        verify(budgetItemService, never()).mapBudgetItemToDTO(any(BudgetItem.class));
    }
}
