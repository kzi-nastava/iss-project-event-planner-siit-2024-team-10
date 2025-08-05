package com.ftn.iss.eventPlanner.selenium;

import com.ftn.iss.eventPlanner.selenium.page.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BudgetTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "http://localhost:4200";

    // Constants for test data
    private static final String LOGIN_EMAIL = "organizer@mail.com";
    private static final String LOGIN_PASSWORD = "password123";
    private static final String BUDGET_AMOUNT = "500";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    // Page Objects
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
    public void testAddNewBudgetItem() {
        navigationBarPage.openMenuAndClickBudget();
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        budgetManagerPage.selectFirstEvent();
        budgetManagerPage.clickAddBudgetItem();
        budgetManagerPage.waitForDialog();
        // select first category
        budgetManagerPage.selectCategory(0);
        budgetManagerPage.enterAmount(BUDGET_AMOUNT);
        budgetManagerPage.clickAddInDialog();

        boolean itemFound = budgetManagerPage.isBudgetItemPresent(BUDGET_AMOUNT);
        Assertions.assertTrue(itemFound, "New budget item with amount " + BUDGET_AMOUNT + " should be visible in the table");
    }

    @Test
    @Order(2)
    public void testDeleteBudgetItemFailureDueToReservedOfferings() {
        navigationBarPage.openMenuAndClickBudget();
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);

        budgetManagerPage.selectFirstEvent();
        budgetManagerPage.clickDeleteButton(0);
        String message = budgetManagerPage.getSnackBarMessage();
        Assertions.assertTrue(message.contains("Budget item cannot be deleted because it has offerings"));
    }

    @Test
    @Order(3)
    public void testDeleteBudgetItem() {
        navigationBarPage.openMenuAndClickBudget();
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);

        budgetManagerPage.selectFirstEvent();

        budgetManagerPage.clickDeleteButton(1);
        String message = budgetManagerPage.getSnackBarMessage();
        Assertions.assertTrue(message.contains("Budget item deleted"));
    }

    @Test
    @Order(4)
    public void testReserveServiceWithCategoryNotInPlannedBudget_Manual() {
        OfferingListPage offeringListPage = new OfferingListPage(driver);
        offeringListPage.clickOfferingCardById(12);

        ReservationPage reservationPage = new ReservationPage(driver);
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);

        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForServiceBookingDialog();

        reservationPage.selectEventByIndex(2);
        reservationPage.fillServiceTimeInputs("1200PM", "0400PM");
        reservationPage.confirmServiceBooking();

        offeringDetailsPage.waitForSnackbarWithText("Reservation request is pending! Email confirmation will been sent.");
    }

    @Test
    @Order(5)
    public void testReserveServiceWithCategoryNotInPlannedBudget_Autoconfirm() {
        driver.get(BASE_URL + "/offering/13");
        ReservationPage reservationPage = new ReservationPage(driver);
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);

        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForServiceBookingDialog();

        reservationPage.selectEventByIndex(2);
        reservationPage.fillServiceTimeInputs("1200AM", "0300AM");
        reservationPage.confirmServiceBooking();

        offeringDetailsPage.waitForSnackbarWithText("Reservation successful! Budget updated. Email confirmation has been sent.");

        // Verify budget update
        navigationBarPage.openMenuAndClickBudget();
        budgetManagerPage.selectEventInBudgetPage(2);

        boolean matchFound = budgetManagerPage.findBudgetItemInTable("electronics", "dj service");
        Assertions.assertTrue(matchFound, "No row found with category 'Electronics' and offering 'dj service'.");
        budgetManagerPage.scrollToBottom();
    }

    @Test
    @Order(6)
    public void testBuyProductWithCategoryNotInPlannedBudget() {
        driver.get(BASE_URL + "/offering/10");
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForEventSelectionDialog();
        offeringDetailsPage.selectEventWithDoubleClick(0);
        offeringDetailsPage.confirmProductPurchase();
        offeringDetailsPage.waitForSnackbarWithText("Product reserved successfully!");

        // Verify budget update
        navigationBarPage.openMenuAndClickBudget();
        budgetManagerPage.selectFirstEvent();

        boolean matchFound = budgetManagerPage.findBudgetItemInTable("nova kategorija", "funeral memorial kit");
        Assertions.assertTrue(matchFound, "No row found with category 'Electronics' and offering 'Funeral Memorial Kit'.");
        budgetManagerPage.scrollToBottom();
    }

    @Test
    @Order(7)
    public void testBuyProductWithCategoryInPlannedBudget() {
        driver.get(BASE_URL + "/offering/5");
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);

        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForEventSelectionDialog();
        offeringDetailsPage.selectEventWithDoubleClick(0);
        offeringDetailsPage.confirmProductPurchase();
        offeringDetailsPage.waitForSnackbarWithText("Product reserved successfully!");

        // Verify budget update
        navigationBarPage.openMenuAndClickBudget();
        budgetManagerPage.selectFirstEvent();

        boolean matchFound = budgetManagerPage.findBudgetItemInTable("electronics", "table linens");
        Assertions.assertTrue(matchFound, "No row found with category 'Electronics' and offering 'table linens'.");
        budgetManagerPage.scrollToBottom();
    }

    @Test
    @Order(8)
    public void testBuyAlreadyPurchasedProduct() {
        driver.get(BASE_URL + "/offering/10");
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);

        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForEventSelectionDialog();
        offeringDetailsPage.selectEventByIndex(0);
        offeringDetailsPage.confirmProductPurchase();
        offeringDetailsPage.waitForSnackbarWithText("This product has already been purchased.");
    }

    @Test
    @Order(9)
    public void testReserveServicePriceOutOfBudget() {
        driver.get(BASE_URL + "/offering/14");
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);
        ReservationPage reservationPage = new ReservationPage(driver);

        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForServiceBookingDialog();

        reservationPage.selectEventByIndex(2);
        reservationPage.fillServiceTimeInputs("0600PM", "1000PM");
        reservationPage.confirmServiceBooking();

        offeringDetailsPage.waitForSnackbarWithText("Not enough budget to record the purchase.");
    }

    @Test
    @Order(10)
    public void testBuyProductWithPriceOutOfBudget() {
        driver.get(BASE_URL + "/offering/3");
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);

        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForEventSelectionDialog();
        offeringDetailsPage.selectEventByIndex(0);
        offeringDetailsPage.confirmProductPurchase();
        offeringDetailsPage.waitForSnackbarWithText("Insufficient budget for this purchase.");
    }

    @Test
    @Order(11)
    public void testBuyNotAvailableProduct() {
        driver.get(BASE_URL + "/offering/8");
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);

        boolean isDisabled = !offeringDetailsPage.isBookNowButtonEnabled();
        Assertions.assertTrue(isDisabled, "Book Now button should be disabled for offering 8.");
    }

    @Test
    @Order(12)
    public void testUpdateToSmallAmount() {
        navigationBarPage.openMenuAndClickBudget();
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        budgetManagerPage.selectFirstEvent();

        budgetManagerPage.updateFirstRowAmount("0");
        budgetManagerPage.clickBody(); // trigger blur

        budgetManagerPage.waitForSnackbarWithText("Failed to update amount");
    }

    @Test
    @Order(13)
    public void testUpdateToBigAmount() {
        navigationBarPage.openMenuAndClickBudget();
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        budgetManagerPage.selectFirstEvent();

        budgetManagerPage.updateFirstRowAmount("160000");
        budgetManagerPage.pressTab();

        String successMessage = budgetManagerPage.getSnackBarMessage();
        Assertions.assertTrue(successMessage.toLowerCase().contains("updated"),
                "Expected success message on valid amount");
    }

    @Test
    @Order(14)
    public void testReserveServiceWithCategoryInPlannedBudget_Autoconfirm() {
        driver.get(BASE_URL + "/offering/19");
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);
        ReservationPage reservationPage = new ReservationPage(driver);
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);

        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForServiceBookingDialog();

        reservationPage.selectEventByIndex(0);
        reservationPage.fillServiceTimeInputs("1200AM", "0200AM");
        reservationPage.confirmServiceBooking();

        offeringDetailsPage.waitForSnackbarWithText("Reservation successful! Budget updated. Email confirmation has been sent.");

        // Verify budget update
        navigationBarPage.openMenuAndClickBudget();
        budgetManagerPage.selectEventInBudgetPage(0);

        boolean matchFound = budgetManagerPage.findBudgetItemInTable("nova kategorija", "party balloon setup");
        Assertions.assertTrue(matchFound, "No row found with category 'Nova kategorija' and offering 'Party Balloon Setup'.");
        budgetManagerPage.scrollToBottom();
    }

    @Test
    @Order(15)
    public void testReserveAlreadyReservedService() {
        driver.get(BASE_URL + "/offering/19");
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);
        ReservationPage reservationPage = new ReservationPage(driver);

        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForServiceBookingDialog();

        reservationPage.selectEventByIndex(0);
        reservationPage.fillServiceTimeInputs("1200AM", "0300AM");
        reservationPage.confirmServiceBooking();

        offeringDetailsPage.waitForSnackbarWithText("You've already made a reservation for selected event.");
    }
}