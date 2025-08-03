package com.ftn.iss.eventPlanner.selenium;

import com.ftn.iss.eventPlanner.selenium.page.CreateEventPage;
import com.ftn.iss.eventPlanner.selenium.page.EventDetailsPage;
import com.ftn.iss.eventPlanner.selenium.page.LoginPage;
import com.ftn.iss.eventPlanner.selenium.page.NavigationBarPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EventTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "http://localhost:4200";
    private LoginPage loginPage;
    private NavigationBarPage navigationBarPage;

    // Constants for test data
    private static final String LOGIN_EMAIL = "organizer@mail.com";
    private static final String LOGIN_PASSWORD = "password123";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
    private static final int UI_REFRESH_DELAY = 5000;
    private static final int SCROLL_DELAY = 1000;
    private static final int DATA_LOAD_DELAY = 2000;



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

    @Test
    public void create_WithValidData_CreatesEvent() {
        navigationBarPage.openMenuAndClickCreateEvent();
        CreateEventPage createEventPage = new CreateEventPage(driver);
        createEventPage.selectFirstEventType();
        createEventPage.fillForm(
                "Tech Conference 2025",    // name
                "A major international conference on emerging technologies.", // description
                150,                      // max participants
                "Serbia",                // country
                "Novi Sad",              // city
                "Bulevar oslobođenja",   // street
                "12B",                   // house number
                LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),            // date
                true                     // publicity
        );
        createEventPage.submitForm();
        wait.until(ExpectedConditions.urlContains("/event"));

        String currentUrl = driver.getCurrentUrl();
        Assertions.assertTrue(currentUrl.contains("/event"),
                "Expected to be redirected to /events after creation, but URL was: " + currentUrl);

        EventDetailsPage eventDetailsPage = new EventDetailsPage(driver);
        eventDetailsPage.waitForFetch();
        Assertions.assertEquals("Tech Conference 2025", eventDetailsPage.getEventName(),
                "Event name should match the created event name.");
    }

    @Test
    public void create_WithNoEventType_CreatesEventWithoutType() {
        navigationBarPage.openMenuAndClickCreateEvent();
        CreateEventPage createEventPage = new CreateEventPage(driver);

        // Check the "No event types apply" checkbox instead of selecting a type
        createEventPage.noEventTypeCheckbox.click();

        createEventPage.fillForm(
                "Unique Gathering",      // name
                "A one-of-a-kind event that doesn't fit standard categories.", // description
                50,                      // max participants
                "Serbia",                // country
                "Kragujevac",            // city
                "Svetozara Markovića",   // street
                "36",                    // house number
                LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),            // date
                true                     // publicity
        );
        createEventPage.submitForm();
        wait.until(ExpectedConditions.urlContains("/event"));

        EventDetailsPage eventDetailsPage = new EventDetailsPage(driver);
        eventDetailsPage.waitForFetch();
        Assertions.assertEquals("Unique Gathering", eventDetailsPage.getEventName(),
                "Event name should match the created event name.");
    }

    @Test
    public void create_EmptyFields_DisablesSubmit() {
        navigationBarPage.openMenuAndClickCreateEvent();
        CreateEventPage createEventPage = new CreateEventPage(driver);

        createEventPage.fillForm(
                "Unique Gathering",      // name
                "A one-of-a-kind event that doesn't fit standard categories.", // description
                50,                      // max participants
                "Serbia",                // country
                "Kragujevac",            // city
                "Svetozara Markovića",   // street
                "36",                    // house number
                LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),            // date
                true                     // publicity
        );
        createEventPage.selectFirstEventType();

        // Verify submit button is enabled with all fields filled
        Assertions.assertTrue(createEventPage.submitButton.isEnabled(),
                "Submit button should be enabled when all required fields are filled.");

        // Clear name field
        createEventPage.clearInput(createEventPage.nameInput);
        Assertions.assertFalse(createEventPage.submitButton.isEnabled(),
                "Submit button should become disabled after clearing name field.");

        // Restore name and clear description
        createEventPage.setName("Complete Test Event");
        Assertions.assertTrue(createEventPage.submitButton.isEnabled(),
                "Submit button should be enabled again after restoring name.");

        createEventPage.clearInput(createEventPage.descriptionInput);
        Assertions.assertFalse(createEventPage.submitButton.isEnabled(),
                "Submit button should become disabled after clearing description field.");

        // Restore description and clear country
        createEventPage.setDescription("Testing progressive form clearing");
        Assertions.assertTrue(createEventPage.submitButton.isEnabled(),
                "Submit button should be enabled again after restoring description.");

        createEventPage.clearInput(createEventPage.countryInput);
        Assertions.assertFalse(createEventPage.submitButton.isEnabled(),
                "Submit button should become disabled after clearing country field.");

        // Restore country and clear city
        createEventPage.setCountry("Serbia");
        Assertions.assertTrue(createEventPage.submitButton.isEnabled(),
                "Submit button should be enabled again after restoring country.");

        createEventPage.clearInput(createEventPage.cityInput);
        Assertions.assertFalse(createEventPage.submitButton.isEnabled(),
                "Submit button should become disabled after clearing city field.");

        // Restore city and clear street
        createEventPage.setCity("Belgrade");
        Assertions.assertTrue(createEventPage.submitButton.isEnabled(),
                "Submit button should be enabled again after restoring city.");

        createEventPage.clearInput(createEventPage.streetInput);
        Assertions.assertFalse(createEventPage.submitButton.isEnabled(),
                "Submit button should become disabled after clearing street field.");

        // Restore street and clear house number
        createEventPage.setStreet("Bulevar oslobođenja");
        Assertions.assertTrue(createEventPage.submitButton.isEnabled(),
                "Submit button should be enabled again after restoring street.");

        createEventPage.clearInput(createEventPage.houseNumberInput);
        Assertions.assertFalse(createEventPage.submitButton.isEnabled(),
                "Submit button should become disabled after clearing house number field.");

        // Restore house number and clear date
        createEventPage.setHouseNumber("12B");
        Assertions.assertTrue(createEventPage.submitButton.isEnabled(),
                "Submit button should be enabled again after restoring house number.");

        createEventPage.clearInput(createEventPage.dateInput);
        Assertions.assertFalse(createEventPage.submitButton.isEnabled(),
                "Submit button should become disabled after clearing date field.");

        // Restore date
        createEventPage.setDate(LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        Assertions.assertTrue(createEventPage.submitButton.isEnabled(),
                "Submit button should be enabled again after restoring date.");

    }

    @Test
    public void create_PastDate_DisablesSubmit() {
        navigationBarPage.openMenuAndClickCreateEvent();
        CreateEventPage createEventPage = new CreateEventPage(driver);
        createEventPage.selectFirstEventType();
        createEventPage.fillForm(
                "Unique Gathering",      // name
                "A one-of-a-kind event that doesn't fit standard categories.", // description
                50,                      // max participants
                "Serbia",                // country
                "Kragujevac",            // city
                "Svetozara Markovića",   // street
                "36",                    // house number
                LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),            // date
                true                     // publicity
        );

        Assertions.assertTrue(createEventPage.submitButton.isEnabled(),
                "Submit button should be enabled with walid date.");

        createEventPage.clearInput(createEventPage.dateInput);
        createEventPage.setDate(LocalDate.now().minusDays(5).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        Assertions.assertFalse(createEventPage.submitButton.isEnabled(),
                "Submit button should be disabled with past date.");
    }
}
