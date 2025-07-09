package com.ftn.iss.eventPlanner.service;

import com.ftn.iss.eventPlanner.dto.budgetitem.CreateBudgetItemDTO;
import com.ftn.iss.eventPlanner.dto.budgetitem.CreatedBudgetItemDTO;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.HashSet;
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

    @InjectMocks
    private BudgetItemService budgetItemService;

    private static final int VALID_EVENT_ID = 1;
    private static final int VALID_CATEGORY_ID = 1;
    private static final int VALID_OFFERING_ID = 1;
    private static final int INVALID_EVENT_ID = 999;
    private static final int INVALID_CATEGORY_ID = 999;
    private static final int INVALID_OFFERING_ID = 999;

    private Event event;
    private OfferingCategory category;
    private CreateBudgetItemDTO createBudgetItemDTO;
    private BudgetItem budgetItem;
    private CreatedBudgetItemDTO createdBudgetItemDTO;
    private Service service;
    private Product product;
    private ServiceDetails serviceDetails;
    private ProductDetails productDetails;

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
        budgetItem.setAmount(100);
        budgetItem.setDeleted(false);
        budgetItem.setEvent(event);
        budgetItem.setCategory(category);
        budgetItem.setServices(new HashSet<>());
        budgetItem.setProducts(new HashSet<>());

        // Setup CreatedBudgetItemDTO
        createdBudgetItemDTO = new CreatedBudgetItemDTO();
        createdBudgetItemDTO.setAmount(100);

        // Setup Service and ServiceDetails
        serviceDetails = new ServiceDetails();
        service = new Service();
        service.setId(VALID_OFFERING_ID);
        service.setCurrentDetails(serviceDetails);

        // Setup Product and ProductDetails
        productDetails = new ProductDetails();
        product = new Product();
        product.setId(VALID_OFFERING_ID);
        product.setCurrentDetails(productDetails);
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
                .hasMessage("Offering cateogry with ID " + INVALID_CATEGORY_ID + " not found");

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

}
/*
    @Test
    public void create_WhenAllInputsAreValid_ReturnsCreatedBudgetItemDTO() {
        // Arrange
        Event event = new Event();
        event.setId(VALID_EVENT_ID);
        event.setBudget(new HashSet<>());

        OfferingCategory category = new OfferingCategory();
        category.setId(VALID_CATEGORY_ID);

        CreateBudgetItemDTO dto = new CreateBudgetItemDTO();
        dto.setAmount(100);
        dto.setCategoryId(VALID_CATEGORY_ID);

        BudgetItem budgetItem = new BudgetItem();
        budgetItem.setId(1);
        budgetItem.setAmount(100);
        budgetItem.setCategory(category);
        budgetItem.setEvent(event);

        when(eventRepository.findById(VALID_EVENT_ID)).thenReturn(Optional.of(event));
        when(offeringCategoryRepository.findById(VALID_CATEGORY_ID)).thenReturn(Optional.of(category));
        when(budgetItemRepository.save(any(BudgetItem.class))).thenReturn(budgetItem);
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        // Act
        CreatedBudgetItemDTO result = budgetItemService.create(VALID_EVENT_ID, dto, VALID_OFFERING_ID);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(100);
        assertThat(result.getCategoryId()).isEqualTo(VALID_CATEGORY_ID);

        // Verify
        verify(eventRepository).findById(VALID_EVENT_ID);
        verify(offeringCategoryRepository).findById(VALID_CATEGORY_ID);
        verify(offeringRepository).findById(VALID_OFFERING_ID);
        verify(budgetItemRepository).save(any(BudgetItem.class));
        verify(eventRepository).save(any(Event.class));
        verifyNoMoreInteractions(eventRepository, offeringCategoryRepository, offeringRepository, budgetItemRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    public void findById_WhenBudgetItemExists_ReturnsGetBudgetItemDTO() {
        // Arrange
        BudgetItem item = new BudgetItem();
        item.setId(1);
        item.setAmount(50);

        when(budgetItemRepository.findById(1)).thenReturn(Optional.of(item));

        // Act
        GetBudgetItemDTO result = budgetItemService.findById(1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);

        verify(budgetItemRepository).findById(1);
        verifyNoMoreInteractions(budgetItemRepository);
    }

    @Test
    public void findById_WhenBudgetItemDoesNotExist_ThrowsIllegalArgumentException() {
        // Arrange
        when(budgetItemRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> budgetItemService.findById(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Budget item with ID 999 not found");

        verify(budgetItemRepository).findById(999);
        verifyNoMoreInteractions(budgetItemRepository);
    }

    @Test
    public void findAll_WhenCalled_ReturnsListOfGetBudgetItemDTO() {
        // Arrange
        BudgetItem item = new BudgetItem();
        item.setId(1);

        when(budgetItemRepository.findAll()).thenReturn(List.of(item));

        // Act
        List<GetBudgetItemDTO> result = budgetItemService.findAll();

        // Assert
        assertThat(result).hasSize(1);

        verify(budgetItemRepository).findAll();
        verifyNoMoreInteractions(budgetItemRepository);
    }

    @Test
    public void updateAmount_WhenNewAmountIsValid_UpdatesAndReturnsUpdatedBudgetItemDTO() {
        // Arrange
        BudgetItem item = new BudgetItem();
        item.setId(1);
        item.setAmount(100);
        item.setServices(new HashSet<>());
        item.setProducts(new HashSet<>());

        when(budgetItemRepository.findById(1)).thenReturn(Optional.of(item));
        when(budgetItemRepository.save(any())).thenReturn(item);

        // Act
        UpdatedBudgetItemDTO result = budgetItemService.updateAmount(1, 200);

        // Assert
        assertThat(result.getAmount()).isEqualTo(200);

        verify(budgetItemRepository).findById(1);
        verify(budgetItemRepository).save(item);
        verifyNoMoreInteractions(budgetItemRepository);
    }

    @Test
    public void updateAmount_WhenNewAmountIsTooSmall_ThrowsIllegalArgumentException() {
        ServiceDetails service = new ServiceDetails();
        service.setPrice(100);
        service.setDiscount(0);

        BudgetItem item = new BudgetItem();
        item.setServices(Set.of(service));
        item.setProducts(new HashSet<>());

        when(budgetItemRepository.findById(1)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> budgetItemService.updateAmount(1, 50))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("New amount cannot be less than");
    }


    @Test
    public void delete_WhenBudgetItemIsEmpty_ReturnsTrue() {
        // Arrange
        BudgetItem item = new BudgetItem();
        item.setServices(new HashSet<>());
        item.setProducts(new HashSet<>());

        Event event = new Event();
        event.setBudget(new HashSet<>(List.of(item)));

        when(budgetItemRepository.findById(1)).thenReturn(Optional.of(item));
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));

        // Act
        boolean result = budgetItemService.delete(1, 1);

        // Assert
        assertThat(result).isTrue();

        verify(budgetItemRepository).findById(1);
        verify(eventRepository).findById(1);
        verifyNoMoreInteractions(budgetItemRepository, eventRepository);
    }

    @Test
    public void delete_WhenBudgetItemContainsOfferings_ReturnsFalse() {
        // Arrange
        ProductDetails product = new ProductDetails();

        BudgetItem item = new BudgetItem();
        item.setProducts(Set.of(product));

        when(budgetItemRepository.findById(1)).thenReturn(Optional.of(item));

        // Act
        boolean result = budgetItemService.delete(1, 1);

        // Assert
        assertThat(result).isFalse();

        verify(budgetItemRepository).findById(1);
        verifyNoMoreInteractions(budgetItemRepository);
    }

    @Test
    public void hasMoneyLeft_WhenEnoughMoney_ReturnsTrue() {
        // Arrange
        BudgetItem item = new BudgetItem();
        item.setAmount(100);
        item.setProducts(new HashSet<>());
        item.setServices(new HashSet<>());

        // Act
        boolean result = budgetItemService.hasMoneyLeft(item, 50, 0);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void getTotalBudgetForEvent_WhenCalled_ReturnsSumExcludingDeletedItems() {
        // Arrange
        BudgetItem item1 = new BudgetItem();
        item1.setAmount(100);
        BudgetItem item2 = new BudgetItem();
        item2.setAmount(200);
        item2.setDeleted(true);

        Event event = new Event();
        event.setBudget(Set.of(item1, item2));

        when(eventRepository.findById(1)).thenReturn(Optional.of(event));

        // Act
        double sum = budgetItemService.getTotalBudgetForEvent(1);

        // Assert
        assertThat(sum).isEqualTo(100);

        verify(eventRepository).findById(1);
        verifyNoMoreInteractions(eventRepository);
    }
}
*/