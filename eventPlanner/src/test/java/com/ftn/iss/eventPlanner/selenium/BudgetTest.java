package com.ftn.iss.eventPlanner.selenium;

import com.ftn.iss.eventPlanner.selenium.page.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BudgetTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "http://localhost:4200";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/eventplanner";
    private static final String DB_USER = "user";
    private static final String DB_PASS = "password";
    private static final String LOGIN_EMAIL = "organizer@mail.com";
    private static final String LOGIN_PASSWORD = "password123";
    private static final String BUDGET_AMOUNT = "500";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    private LoginPage loginPage;
    private NavigationBarPage navigationBarPage;
    @BeforeAll
    public void resetDatabaseBeforeTests() {
        executeSqlScript();
    }
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
            driver = null;
        }
    }

    @Test
    @Order(1)
    public void reserveService_inPlannedBudgetWithAutoConfirm_updatesBudget() {
        OfferingListPage offeringListPage = new OfferingListPage(driver);
        offeringListPage.searchAndClickOffering("Wedding Photography", true);
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
        boolean matchFound = budgetManagerPage.findBudgetItemInTable("electronics", "Wedding Photography");
        Assertions.assertTrue(matchFound, "No row found with category 'electronics' and offering 'Wedding Photography'.");
        budgetManagerPage.scrollToBottom();
    }

    @Test
    @Order(2)
    public void reserveService_alreadyBooked_showsAlreadyReservedMessage() {
        OfferingListPage offeringListPage = new OfferingListPage(driver);
        offeringListPage.searchAndClickOffering("Wedding Photography", true);
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
    @Order(3)
    public void addBudgetItem_validData_displaysSnackBar() {
        navigationBarPage.openMenuAndClickBudget();
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        budgetManagerPage.selectEventByName("Tech Workshop");
        budgetManagerPage.clickAddBudgetItem();
        budgetManagerPage.waitForDialog();
        budgetManagerPage.selectCategory(0);
        budgetManagerPage.enterAmount(BUDGET_AMOUNT);
        budgetManagerPage.clickAddInDialog();

        String message = budgetManagerPage.getSnackBarMessage();
        Assertions.assertTrue(message.contains("Budget item added"));
    }

    @Test
    @Order(4)
    public void deleteBudgetItem_withReservedOfferings_showsErrorMessage() {
        navigationBarPage.openMenuAndClickBudget();
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        budgetManagerPage.selectEventByName("Tech Workshop");
        budgetManagerPage.clickDeleteButton(0);
        String message = budgetManagerPage.getSnackBarMessage();
        Assertions.assertTrue(message.contains("Budget item cannot be deleted because it has offerings"));
    }

    @Test
    @Order(5)
    public void deleteBudgetItem_withoutOfferings_succeeds() {
        navigationBarPage.openMenuAndClickBudget();
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        budgetManagerPage.selectEventByName("Tech Workshop");
        budgetManagerPage.clickDeleteButton(1);
        String message = budgetManagerPage.getSnackBarMessage();
        Assertions.assertTrue(message.contains("Budget item deleted"));
    }

    @Test
    @Order(6)
    public void reserveService_notInPlannedBudgetWithManualConfirmation_succeeds() {
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
    @Order(7)
    public void reserveService_notInPlannedBudgetWithAutoConfirm_updatesBudget() {
        OfferingListPage offeringListPage = new OfferingListPage(driver);
        offeringListPage.searchAndClickOffering("DJ Service", true);
        ReservationPage reservationPage = new ReservationPage(driver);
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);
        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForServiceBookingDialog();
        reservationPage.selectEventByName("Charity Gala");
        reservationPage.fillServiceTimeInputs("1200AM", "0300AM");
        reservationPage.confirmServiceBooking();
        offeringDetailsPage.waitForSnackbarWithText("Reservation successful! Budget updated. Email confirmation has been sent.");
        navigationBarPage.openMenuAndClickBudget();
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        budgetManagerPage.selectEventByName("Charity Gala");
        boolean matchFound = budgetManagerPage.findBudgetItemInTable("electronics", "dj service");
        Assertions.assertTrue(matchFound, "No row found with category 'Electronics' and offering 'dj service'.");
        budgetManagerPage.scrollToBottom();
    }

    @Test
    @Order(8)
    public void buyProduct_notInPlannedBudget_updatesBudget() {
        OfferingListPage offeringListPage = new OfferingListPage(driver);
        offeringListPage.searchAndClickOffering("Conference Projector", false);
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);
        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForEventSelectionDialog();
        offeringDetailsPage.selectEventByName("Tech Workshop");
        offeringDetailsPage.confirmProductPurchase();
        offeringDetailsPage.waitForSnackbarWithText("Product successfully added to budget.");
        navigationBarPage.openMenuAndClickBudget();
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        budgetManagerPage.selectEventByName("Tech Workshop");
        boolean matchFound = budgetManagerPage.findBudgetItemInTable("Home Services", "Conference Projector");
        Assertions.assertTrue(matchFound, "No row found with category 'Home Services' and offering 'Conference Projector'.");
        budgetManagerPage.scrollToBottom();
    }

    @Test
    @Order(9)
    public void buyProduct_inPlannedBudget_updatesBudget() {
        OfferingListPage offeringListPage = new OfferingListPage(driver);
        offeringListPage.searchAndClickOffering("Table Linens", false);
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);
        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForEventSelectionDialog();
        offeringDetailsPage.selectEventByName("Tech Workshop");
        offeringDetailsPage.confirmProductPurchase();
        offeringDetailsPage.waitForSnackbarWithText("Product successfully added to budget.");
        navigationBarPage.openMenuAndClickBudget();
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        budgetManagerPage.selectEventByName("Tech Workshop");
        boolean matchFound = budgetManagerPage.findBudgetItemInTable("electronics", "table linens");
        Assertions.assertTrue(matchFound, "No row found with category 'Electronics' and offering 'table linens'.");
        budgetManagerPage.scrollToBottom();
    }

    @Test
    @Order(10)
    public void buyProduct_alreadyPurchased_showsAlreadyPurchasedMessage() {
        OfferingListPage offeringListPage = new OfferingListPage(driver);
        offeringListPage.searchAndClickOffering("Table Linens", false);
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);
        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForEventSelectionDialog();
        offeringDetailsPage.selectEventByName("Tech Workshop");
        offeringDetailsPage.confirmProductPurchase();
        offeringDetailsPage.waitForSnackbarWithText("Product already purchased");
    }

    @Test
    @Order(11)
    public void reserveService_exceedsBudget_showsInsufficientBudgetError() {
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
    @Order(12)
    public void buyProduct_exceedsBudget_showsInsufficientBudgetError() {
        OfferingListPage offeringListPage = new OfferingListPage(driver);
        offeringListPage.searchAndClickOffering("Stage Lighting Kit", false);
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);
        offeringDetailsPage.clickBookNowButton();
        offeringDetailsPage.waitForEventSelectionDialog();
        offeringDetailsPage.selectEventByName("Tech Workshop");
        offeringDetailsPage.confirmProductPurchase();
        offeringDetailsPage.waitForSnackbarWithText("Insufficient budget for this purchase");
    }

    @Test
    @Order(13)
    public void viewUnavailableProduct_default_disablesBookNowButton() {
        OfferingListPage offeringListPage = new OfferingListPage(driver);
        offeringListPage.searchAndClickOffering("Wedding Decoration Set", false);
        OfferingDetailsPage offeringDetailsPage = new OfferingDetailsPage(driver);
        boolean isDisabled = !offeringDetailsPage.isBookNowButtonEnabled();
        Assertions.assertTrue(isDisabled, "Book Now button should be disabled for offering 8.");
    }

    @Test
    @Order(14)
    public void updateBudgetAmount_tooSmall_showsError() {
        navigationBarPage.openMenuAndClickBudget();
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        budgetManagerPage.selectEventByName("Tech Workshop");
        budgetManagerPage.updateFirstRowAmount("0");
        budgetManagerPage.clickBody();
        budgetManagerPage.waitForSnackbarWithText("Failed to update amount");
    }

    @Test
    @Order(15)
    public void updateBudgetAmount_largeValue_succeeds() {
        navigationBarPage.openMenuAndClickBudget();
        BudgetManagerPage budgetManagerPage = new BudgetManagerPage(driver);
        budgetManagerPage.selectEventByName("Tech Workshop");
        budgetManagerPage.updateFirstRowAmount("160000");
        budgetManagerPage.pressTab();
        String successMessage = budgetManagerPage.getSnackBarMessage();
        Assertions.assertTrue(successMessage.toLowerCase().contains("updated"), "Expected success message on valid amount");
    }

    @Test
    @Order(16)
    public void validateBudgetAmount_expectedValue_matchesExpectedValue() {
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


    private void executeSqlScript() {
        String resetScript = """
        DELETE FROM budget_item_services;
        DELETE FROM budget_item_products;
        DELETE FROM budget_item;
        DELETE FROM reservation;
        
        INSERT INTO budget_item (amount, is_deleted, category_id, event_id) VALUES
            (1200.0, false, 1, 1),
            (100, false, 1, 2);
                    """;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {

            String[] statements = resetScript.split(";");
            for (String statement : statements) {
                if (!statement.trim().isEmpty()) {
                    stmt.executeUpdate(statement.trim());
                }
            }
            System.out.println("Database reset completed successfully");

        } catch (SQLException e) {
            System.err.println("Database reset failed: " + e.getMessage());
        }
    }

}