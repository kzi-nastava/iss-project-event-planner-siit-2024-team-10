package com.ftn.iss.eventPlanner.selenium;

import com.ftn.iss.eventPlanner.selenium.page.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BudgetTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "http://localhost:4200";

    private static final String LOGIN_EMAIL = "organizer@mail.com";
    private static final String LOGIN_PASSWORD = "password123";
    private static final String BUDGET_AMOUNT = "500";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    private LoginPage loginPage;
    private NavigationBarPage navigationBarPage;

    @BeforeEach
    public void setup() {
        driver = new ChromeDriver();
        driver.get(BASE_URL);
        wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        driver.manage().window().maximize();
        navigationBarPage = new NavigationBarPage(driver);
        navigationBarPage.openLoginPage();
        loginPage = new LoginPage(driver);
        loginPage.login(LOGIN_EMAIL, LOGIN_PASSWORD);
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    public void addNewBudgetItem_shouldDisplayInTable() {
        navigationBarPage.openMenuAndClickBudget();
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        budgetManagerPage.selectEventByName("Tech Workshop");
        budgetManagerPage.clickAddBudgetItem();
        budgetManagerPage.waitForDialog();
        budgetManagerPage.selectCategory(0);
        budgetManagerPage.enterAmount(BUDGET_AMOUNT);
        budgetManagerPage.clickAddInDialog();

        boolean itemFound = budgetManagerPage.isBudgetItemPresent(BUDGET_AMOUNT);
        Assertions.assertTrue(itemFound, "New budget item with amount " + BUDGET_AMOUNT + " should be visible in the table");
    }

    @Test
    @Order(2)
    public void deleteBudgetItem_withReservedOfferings_shouldShowErrorMessage() {
        navigationBarPage.openMenuAndClickBudget();
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        budgetManagerPage.selectEventByName("Tech Workshop");
        budgetManagerPage.clickDeleteButton(0);
        String message = budgetManagerPage.getSnackBarMessage();
        Assertions.assertTrue(message.contains("Budget item cannot be deleted because it has offerings"));
    }

    @Test
    @Order(3)
    public void deleteBudgetItem_withoutOfferings_shouldSucceed() {
        navigationBarPage.openMenuAndClickBudget();
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        budgetManagerPage.selectEventByName("Tech Workshop");
        budgetManagerPage.clickDeleteButton(1);
        String message = budgetManagerPage.getSnackBarMessage();
        Assertions.assertTrue(message.contains("Budget item deleted"));
    }

    @Test
    @Order(4)
    public void reserveService_notInPlannedBudget_manualConfirmation_shouldSucceed() {
        OfferingListPage offeringListPage = new OfferingListPage(driver);
        offeringListPage.searchAndClickOffering("Event Catering", true);
        ReservationPage reservationPage = new ReservationPage(driver);
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);
        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForServiceBookingDialog();
        reservationPage.selectEventByName("Charity Gala");
        reservationPage.fillServiceTimeInputs("1200PM", "0400PM");
        reservationPage.confirmServiceBooking();
        offeringDetailsPage.waitForSnackbarWithText("Reservation request is pending! Email confirmation will been sent.");
    }

    @Test
    @Order(5)
    public void reserveService_notInPlannedBudget_autoConfirm_shouldUpdateBudget() {
        OfferingListPage offeringListPage = new OfferingListPage(driver);
        offeringListPage.searchAndClickOffering("DJ Service", true);
        ReservationPage reservationPage = new ReservationPage(driver);
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForServiceBookingDialog();
        reservationPage.selectEventByName("Charity Gala");
        reservationPage.fillServiceTimeInputs("1200AM", "0300AM");
        reservationPage.confirmServiceBooking();
        offeringDetailsPage.waitForSnackbarWithText("Reservation successful! Budget updated. Email confirmation has been sent.");
        navigationBarPage.openMenuAndClickBudget();
        budgetManagerPage.selectEventByName("Charity Gala");
        boolean matchFound = budgetManagerPage.findBudgetItemInTable("electronics", "dj service");
        Assertions.assertTrue(matchFound, "No row found with category 'Electronics' and offering 'dj service'.");
        budgetManagerPage.scrollToBottom();
    }

    @Test
    @Order(6)
    public void buyProduct_notInPlannedBudget_shouldUpdateBudget() {
        OfferingListPage offeringListPage = new OfferingListPage(driver);
        offeringListPage.searchAndClickOffering("Conference Projector", false);
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForEventSelectionDialog();
        offeringDetailsPage.selectEventByIndex(0);
        offeringDetailsPage.confirmProductPurchase();
        offeringDetailsPage.waitForSnackbarWithText("Product successfully added to budget.");
        navigationBarPage.openMenuAndClickBudget();
        budgetManagerPage.selectEventByName("Tech Workshop");
        boolean matchFound = budgetManagerPage.findBudgetItemInTable("Home Services", "Conference Projector");
        Assertions.assertTrue(matchFound, "No row found with category 'Home Services' and offering 'Conference Projector'.");
        budgetManagerPage.scrollToBottom();
    }

    @Test
    @Order(7)
    public void buyProduct_inPlannedBudget_shouldUpdateBudget() {
        OfferingListPage offeringListPage = new OfferingListPage(driver);
        offeringListPage.searchAndClickOffering("Table Linens", false);
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForEventSelectionDialog();
        offeringDetailsPage.selectEventByIndex(0);
        offeringDetailsPage.confirmProductPurchase();
        offeringDetailsPage.waitForSnackbarWithText("Product successfully added to budget.");
        navigationBarPage.openMenuAndClickBudget();
        budgetManagerPage.selectEventByName("Tech Workshop");
        boolean matchFound = budgetManagerPage.findBudgetItemInTable("electronics", "table linens");
        Assertions.assertTrue(matchFound, "No row found with category 'Electronics' and offering 'table linens'.");
        budgetManagerPage.scrollToBottom();
    }

    @Test
    @Order(8)
    public void buyAlreadyPurchasedProduct_shouldShowAlreadyPurchasedMessage() {
        OfferingListPage offeringListPage = new OfferingListPage(driver);
        offeringListPage.searchAndClickOffering("Table Linens", false);
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);
        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForEventSelectionDialog();
        offeringDetailsPage.selectEventByIndex(0);
        offeringDetailsPage.confirmProductPurchase();
        offeringDetailsPage.waitForSnackbarWithText("Product already purchased");
    }

    @Test
    @Order(9)
    public void reserveService_exceedsBudget_shouldShowInsufficientBudgetError() {
        OfferingListPage offeringListPage = new OfferingListPage(driver);
        offeringListPage.searchAndClickOffering("Event Security", true);
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);
        ReservationPage reservationPage = new ReservationPage(driver);
        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForServiceBookingDialog();
        reservationPage.selectEventByName("Tech Workshop");
        reservationPage.fillServiceTimeInputs("0600PM", "1000PM");
        reservationPage.confirmServiceBooking();
        offeringDetailsPage.waitForSnackbarWithText("Insufficient budget for this purchase");
    }

    @Test
    @Order(10)
    public void buyProduct_exceedsBudget_shouldShowInsufficientBudgetError() {
        OfferingListPage offeringListPage = new OfferingListPage(driver);
        offeringListPage.searchAndClickOffering("Stage Lighting Kit", false);
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);
        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForEventSelectionDialog();
        offeringDetailsPage.selectEventByIndex(0);
        offeringDetailsPage.confirmProductPurchase();
        offeringDetailsPage.waitForSnackbarWithText("Insufficient budget for this purchase");
    }

    @Test
    @Order(11)
    public void buyUnavailableProduct_shouldDisableBookNowButton() {
        OfferingListPage offeringListPage = new OfferingListPage(driver);
        offeringListPage.searchAndClickOffering("Wedding Decoration Set", false);
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);
        boolean isDisabled = !offeringDetailsPage.isBookNowButtonEnabled();
        Assertions.assertTrue(isDisabled, "Book Now button should be disabled for offering 8.");
    }

    @Test
    @Order(12)
    public void updateBudgetAmount_tooSmall_shouldShowError() {
        navigationBarPage.openMenuAndClickBudget();
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        budgetManagerPage.selectEventByName("Tech Workshop");
        budgetManagerPage.updateFirstRowAmount("0");
        budgetManagerPage.clickBody();
        budgetManagerPage.waitForSnackbarWithText("Failed to update amount");
    }

    @Test
    @Order(13)
    public void updateBudgetAmount_largeValue_shouldSucceed() {
        navigationBarPage.openMenuAndClickBudget();
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        budgetManagerPage.selectEventByName("Tech Workshop");
        budgetManagerPage.updateFirstRowAmount("160000");
        budgetManagerPage.pressTab();
        String successMessage = budgetManagerPage.getSnackBarMessage();
        Assertions.assertTrue(successMessage.toLowerCase().contains("updated"), "Expected success message on valid amount");
    }

    @Test
    @Order(14)
    public void reserveService_inPlannedBudget_autoConfirm_shouldUpdateBudget() {
        OfferingListPage offeringListPage = new OfferingListPage(driver);
        offeringListPage.searchAndClickOffering("Party Balloon Setup", true);
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);
        ReservationPage reservationPage = new ReservationPage(driver);
        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForServiceBookingDialog();
        reservationPage.selectEventByName("Tech Workshop");
        reservationPage.fillServiceTimeInputs("1200AM", "0200AM");
        reservationPage.confirmServiceBooking();
        offeringDetailsPage.waitForSnackbarWithText("Reservation successful! Budget updated. Email confirmation has been sent.");
        navigationBarPage.openMenuAndClickBudget();
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        budgetManagerPage.selectEventByName("Tech Workshop");
        boolean matchFound = budgetManagerPage.findBudgetItemInTable("nova kategorija", "party balloon setup");
        Assertions.assertTrue(matchFound, "No row found with category 'Nova kategorija' and offering 'Party Balloon Setup'.");
        budgetManagerPage.scrollToBottom();
    }

    @Test
    @Order(15)
    public void reserveAlreadyBookedService_shouldShowAlreadyReservedMessage() {
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        OfferingListPage offeringListPage = new OfferingListPage(driver);
        offeringListPage.searchAndClickOffering("Party Balloon Setup", true);
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);
        ReservationPage reservationPage = new ReservationPage(driver);
        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForServiceBookingDialog();
        reservationPage.selectEventByName("Tech Workshop");
        reservationPage.fillServiceTimeInputs("1200AM", "0300AM");
        reservationPage.confirmServiceBooking();
        offeringDetailsPage.waitForSnackbarWithText("You've already made a reservation for selected event.");
    }
    @Test
    @Order(16)
    public void validateBudgetAmount_shouldMatchExpectedValue() {
        navigationBarPage.openMenuAndClickBudget();
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        budgetManagerPage.selectEventByName("Tech Workshop");

        double expectedBudget = 160000.00;

        boolean isBudgetCorrect = budgetManagerPage.isTotalBudgetEqualTo(expectedBudget);
        double actualBudget = budgetManagerPage.getTotalBudgetAmount();

        System.out.println("Expected budget: $" + expectedBudget);
        System.out.println("Actual budget: $" + actualBudget);

        Assertions.assertTrue(isBudgetCorrect,
                "Budget should be $" + expectedBudget + " but was $" + actualBudget);
    }
    @Test
    @Order(17)
    public void checkRecommendedCategories_businessConference_shouldHaveCategories() {
        navigationBarPage.openMenuAndClickBudget();
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);

        List<String> expectedCategories = Arrays.asList("Electronics", "Home Services");

        boolean hasCorrectCategories = budgetManagerPage.hasExpectedRecommendedCategories(
                "Business Conference", expectedCategories);

        Assertions.assertTrue(hasCorrectCategories,
                "Business Conference should have Electronics and Home Services");
    }

    @Test
    @Order(18)
    public void checkRecommendedCategories_techWorkshop_shouldHaveNoCategories() {
        navigationBarPage.openMenuAndClickBudget();
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);

        List<String> expectedCategories = new ArrayList<>();

        boolean hasCorrectCategories = budgetManagerPage.hasExpectedRecommendedCategories(
                "Tech Workshop", expectedCategories);

        Assertions.assertTrue(hasCorrectCategories,
                "Tech Workshop should have NO recommended categories");
    }
}
